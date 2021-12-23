package zio.lambda.internal

import sttp.client3._
import zio._
import zio.json._

import scala.concurrent.duration.Duration

final case class RuntimeApiLive(
  sttpBackend: SttpBackend[Identity, Any],
  environment: LambdaEnvironment
) extends RuntimeApi {

  val baseRuntimeUrl = s"http://${environment.runtimeApi}/2018-06-01/runtime"

  def getNextInvocation: Task[InvocationRequest] =
    ZIO.fromEither {
      val response = sttpBackend
        .send(
          basicRequest
            .get(uri"$baseRuntimeUrl/invocation/next")
            .readTimeout(Duration.Inf)
            .response(asString)
        )

      response.body
        .fold(
          errorMessage =>
            Left(new Throwable(s"Error response from Runtime API. message=$errorMessage, code=${response.code.code}")),
          body =>
            InvocationRequest
              .fromHttpResponse(
                response.headers
                  .map(header => header.name -> header.value)
                  .toMap,
                body
              )
              .left
              .map(new Throwable(_))
        )
    }

  def sendInvocationResponse(invocationResponse: InvocationResponse): Task[Unit] =
    ZIO
      .effect(
        sttpBackend.send(
          basicRequest
            .post(uri"$baseRuntimeUrl/invocation/${invocationResponse.requestId.value}/response")
            .body(invocationResponse.payload)
            .response(ignore)
        )
      )
      .unit

  def sendInvocationError(invocationError: InvocationError): Task[Unit] =
    ZIO
      .effect(
        sttpBackend.send(
          basicRequest
            .post(uri"$baseRuntimeUrl/invocation/${invocationError.requestId.value}/error")
            .response(ignore)
            .body(invocationError.errorResponse.toJson)
        )
      )
      .unit

  def sendInitializationError(errorResponse: InvocationErrorResponse): Task[Unit] =
    ZIO
      .effect(
        sttpBackend.send(
          basicRequest
            .post(uri"$baseRuntimeUrl/init/error")
            .header("Lambda-Runtime-Function-Error-Type", errorResponse.errorType)
            .body(errorResponse.toJson)
            .response(ignore)
        )
      )
      .unit

}

object RuntimeApiLive {
  val layer: ZLayer[Has[SttpBackend[Identity, Any]] with Has[LambdaEnvironment], Throwable, Has[RuntimeApi]] =
    (RuntimeApiLive(_, _)).toLayer

}
