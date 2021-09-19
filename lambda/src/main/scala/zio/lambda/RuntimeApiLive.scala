package zio.lambda

import sttp.client3._
import zio._
import zio.blocking._
import zio.json._
import zio.lambda._

import scala.concurrent.duration.Duration

final case class RuntimeApiLive(
  sttpBackend: SttpBackend[Identity, Any],
  environment: LambdaEnvironment,
  blocking: Blocking.Service
) extends RuntimeApi {
  val baseRuntimeUrl = s"http://${environment.runtimeApi}/2018-06-01/runtime"
  override def nextInvocation(): Task[InvocationRequest] =
    blocking
      .effectBlocking(
        sttpBackend.send(
          basicRequest
            .get(uri"$baseRuntimeUrl/invocation/next")
            .readTimeout(Duration.Inf)
            .response(asString)
        )
      )
      .flatMap(response =>
        response.body
          .fold(
            errorMessage =>
              ZIO.fail(
                new Throwable(s"Error response from Runtime API. message=$errorMessage, code=${response.code.code}")
              ),
            body =>
              ZIO
                .fromEither(
                  InvocationRequest
                    .fromHttpResponse(
                      response.headers
                        .map(header => header.name -> header.value)
                        .toMap,
                      body
                    )
                )
                .mapError(new Throwable(_))
          )
      )

  override def invocationResponse(requestId: InvocationRequest.Id, response: String): Task[Unit] =
    blocking
      .effectBlocking(
        sttpBackend.send(
          basicRequest
            .post(uri"$baseRuntimeUrl/invocation/${requestId.value}/response")
            .body(response)
            .response(ignore)
        )
      )
      .unit

  override def invocationError(
    requestId: InvocationRequest.Id,
    error: InvocationError
  ): Task[Unit] =
    blocking
      .effectBlocking(
        sttpBackend.send(
          basicRequest
            .post(uri"$baseRuntimeUrl/invocation/${requestId.value}/error")
            .response(ignore)
            .body(error.toJson)
        )
      )
      .unit

  override def initializationError(error: InvocationError): Task[Unit] =
    blocking
      .effectBlocking(
        sttpBackend.send(
          basicRequest
            .post(uri"$baseRuntimeUrl/init/error")
            .response(ignore)
            .header("Lambda-Runtime-Function-Error-Type", "Unhandled")
            .body(error.toJson)
        )
      )
      .unit
}

object RuntimeApiLive {
  val layer: URLayer[Has[SttpBackend[Identity, Any]] with Has[LambdaEnvironment] with Blocking, Has[RuntimeApi]] =
    (RuntimeApiLive(_, _, _)).toLayer
}
