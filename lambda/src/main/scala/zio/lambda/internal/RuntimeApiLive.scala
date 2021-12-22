package zio.lambda.internal

import sttp.client3._
import zio._
import zio.blocking.Blocking
import zio.json._
import zio.lambda.internal.SttpClient

import scala.concurrent.duration.Duration

final case class RuntimeApiLive(
  blocking: Blocking.Service,
  sttpClient: SttpClient,
  environment: LambdaEnvironment
) extends RuntimeApi {

  val baseRuntimeUrl = s"http://${environment.runtimeApi}/2018-06-01/runtime"

  def getNextInvocation: Task[InvocationRequest] =
    sttpClient.getSttpBackend
      .flatMap(sttpBackend =>
        blocking
          .effectBlocking(
            sttpBackend.send(
              basicRequest
                .get(uri"$baseRuntimeUrl/invocation/next")
                .readTimeout(Duration.Inf)
                .response(asString)
            )
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

  def sendInvocationResponse(invocationResponse: InvocationResponse): Task[Unit] =
    sttpClient.getSttpBackend
      .flatMap(sttpBackend =>
        blocking
          .effectBlocking(
            sttpBackend.send(
              basicRequest
                .post(uri"$baseRuntimeUrl/invocation/${invocationResponse.requestId.value}/response")
                .body(invocationResponse.payload)
                .response(ignore)
            )
          )
          .unit
      )

  def sendInvocationError(invocationError: InvocationError): Task[Unit] =
    sttpClient.getSttpBackend
      .flatMap(sttpBackend =>
        blocking
          .effectBlocking(
            sttpBackend.send(
              basicRequest
                .post(uri"$baseRuntimeUrl/invocation/${invocationError.requestId.value}/error")
                .response(ignore)
                .body(invocationError.errorResponse.toJson)
            )
          )
          .unit
      )

  def sendInitializationError(errorResponse: InvocationErrorResponse): Task[Unit] =
    sttpClient.getSttpBackend
      .flatMap(sttpBackend =>
        blocking
          .effectBlocking(
            sttpBackend.send(
              basicRequest
                .post(uri"$baseRuntimeUrl/init/error")
                .header("Lambda-Runtime-Function-Error-Type", errorResponse.errorType)
                .body(errorResponse.toJson)
                .response(ignore)
            )
          )
          .unit
      )

}

object RuntimeApiLive {

  val layer: ZLayer[Blocking with Has[SttpClient] with Has[LambdaEnvironment], Throwable, Has[RuntimeApi]] =
    (RuntimeApiLive(_, _, _)).toLayer

}
