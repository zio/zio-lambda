package zio.lambda.internal

import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.json._

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import scala.jdk.CollectionConverters._

final case class RuntimeApiLive(
  blocking: Blocking.Service,
  clock: Clock.Service,
  environment: LambdaEnvironment
) extends RuntimeApi {

  val baseRuntimeUrl = s"http://${environment.runtimeApi}/2018-06-01/runtime"

  def getNextInvocation: Task[InvocationRequest] = {
    def readResponse(is: => InputStream) = {
      var exhausted    = false
      val in           = new BufferedReader(new InputStreamReader(is))
      val responseBody = new StringBuffer()
      while (!exhausted)
        Option(in.readLine()) match {
          case Some(line) => responseBody.append(line)
          case None       => exhausted = true
        }
      in.close()
      responseBody.toString()
    }

    ZIO.effect {
      val url = new URL(s"$baseRuntimeUrl/invocation/next")
      val con = url.openConnection().asInstanceOf[HttpURLConnection]
      con.setRequestMethod("GET")
      val responseBody = readResponse(con.getInputStream())
      val headers = con
        .getHeaderFields()
        .asScala
        .map { case (key, values) => key -> values.asScala.headOption.getOrElse("") }
        .toMap
      InvocationRequest.fromHttpResponse(headers, responseBody)

    }.flatMap {
      case Left(value)  => ZIO.fail(new Throwable(value))
      case Right(value) => ZIO.succeed(value)
    }
  }

  def sendInvocationResponse(invocationResponse: InvocationResponse): Task[Unit] = {
    val url  = new URL(s"$baseRuntimeUrl/invocation/${invocationResponse.requestId.value}/response")
    val conn = url.openConnection().asInstanceOf[HttpURLConnection]
    conn.setRequestMethod("POST")
    conn.setRequestProperty("Content-Type", "application/json")
    conn.setFixedLengthStreamingMode(invocationResponse.payload.length())
    conn.setDoOutput(true)

    ZIO.effect {
      val outputStream = conn.getOutputStream()
      outputStream.write(invocationResponse.payload.getBytes())
      conn.getInputStream().close()
    }
  }

  def sendInvocationError(invocationError: InvocationError): Task[Unit] =
    postRequest(
      s"$baseRuntimeUrl/invocation/${invocationError.requestId.value}/error",
      invocationError.errorResponse
    )

  def sendInitializationError(errorResponse: InvocationErrorResponse): Task[Unit] =
    postRequest(
      s"$baseRuntimeUrl/init/error",
      errorResponse,
      Map("Lambda-Runtime-Function-Error-Type" -> errorResponse.errorType)
    )

  private def postRequest[A: JsonEncoder](
    url: String,
    payload: A,
    headers: Map[String, String] = Map.empty
  ): Task[Unit] = {
    val conn = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
    val body = payload.toJson
    conn.setRequestMethod("POST")
    conn.setRequestProperty("Content-Type", "application/json")
    conn.setFixedLengthStreamingMode(body.length())
    conn.setDoOutput(true)

    headers.foreach { case (header, value) =>
      conn.setRequestProperty(header, value)
    }

    ZIO.effect {
      val outputStream = conn.getOutputStream()
      outputStream.write(body.getBytes())
      conn.getInputStream().close()
    }
  }
}

object RuntimeApiLive {
  val layer: ZLayer[Blocking with Clock with Has[LambdaEnvironment], Throwable, Has[RuntimeApi]] =
    (for {
      blocking          <- ZIO.service[Blocking.Service]
      clock             <- ZIO.service[Clock.Service]
      lambdaEnvironment <- LambdaEnvironment.getEnvironment
    } yield RuntimeApiLive(blocking, clock, lambdaEnvironment)).toLayer

}
