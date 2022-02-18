package zio.lambda.internal

import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.json._

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.io.ByteArrayOutputStream

final case class RuntimeApiLive(
  blocking: Blocking.Service,
  clock: Clock.Service,
  environment: LambdaEnvironment
) extends RuntimeApi {

  private val baseRuntimeUrl    = s"http://${environment.runtimeApi}/2018-06-01/runtime"
  private val nextInvocationUrl = new URL(s"$baseRuntimeUrl/invocation/next")

  def getNextInvocation: Task[InvocationRequest] = {
    def readResponse(is: InputStream) = {
      val result = new ByteArrayOutputStream()
      val buffer = new Array[Byte](1024)
      var length = is.read(buffer)
      while (length != -1) {
        result.write(buffer, 0, length)
        length = is.read(buffer)
      }
      try is.close() // Reusing connection
      catch { case _: Throwable => () }

      result.toString("UTF-8")
    }

    ZIO.effect {
      val con = nextInvocationUrl.openConnection().asInstanceOf[HttpURLConnection]
      con.setRequestMethod("GET")
      val responseBody = readResponse(con.getInputStream())
      InvocationRequest.fromHttpResponse(con.getHeaderFields(), responseBody)
    }
  }

  def sendInvocationResponse(invocationResponse: InvocationResponse): Task[Unit] = {
    val url  = new URL(s"$baseRuntimeUrl/invocation/${invocationResponse.requestId}/response")
    val conn = url.openConnection().asInstanceOf[HttpURLConnection]
    conn.setRequestMethod("POST")
    conn.setRequestProperty("Content-Type", "application/json")
    conn.setFixedLengthStreamingMode(invocationResponse.payload.length())
    conn.setDoOutput(true)

    ZIO.effect {
      val outputStream = conn.getOutputStream()
      outputStream.write(invocationResponse.payload.getBytes())
      try conn.getInputStream().close()
      catch { case _: Throwable => () } // Reusing connection
    }
  }

  def sendInvocationError(invocationError: InvocationError): Task[Unit] =
    postRequest(
      s"$baseRuntimeUrl/invocation/${invocationError.requestId}/error",
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
