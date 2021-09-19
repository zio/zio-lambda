package zio.lambda

import sttp.client3.HttpURLConnectionBackend
import zio._
import zio.blocking.Blocking
import zio.console._
import zio.json._
import zio.lambda.RuntimeApi._
import zio.lambda._

/**
 * Implementation example:
 *
 * {{{
 * object KinesisLambda extends ZLambda[MyPayload, MyResponse] {
 *  def handle(request: MyPayload): ZIO[ZEnv, Throwable, MyResponse] = ???
 * }
 *
 * final case class MyPayload(value: String)
 *
 * object MyPayload {
 *  import zio.json.JsonDecoder
 *  import zio.json.DeriveJsonDecoder
 *
 *  implicit val jsonDecoder: JsonDecoder[MyPayload] = DeriveJsonDecoder.gen
 * }
 *
 * final case class MyResponse(value: String)
 *
 * object MyResponse {
 *  import zio.json.JsonEncoder
 *  import zio.json.DeriveJsonEncoder
 *
 *  implicit val jsonEncoder: JsonEncoder[MyResponse] = DeriveJsonEncoder.gen
 * }
 * }}}
 */
abstract class ZLambda[R, A](
  implicit val lambdaEventDecoder: JsonDecoder[R],
  implicit val lambdaResponseEncoder: JsonEncoder[A]
) extends App {

  def handle(event: R): ZIO[ZEnv, Throwable, A]

  final override def run(args: List[String]): URIO[ZEnv, ExitCode] = {

    val runtimeApiLayer = (
      LambdaEnvironment.live ++
        Blocking.live ++
        ZLayer.succeed(HttpURLConnectionBackend())
    ) >>> RuntimeApiLive.layer

    val runtimeLayer = runtimeApiLayer ++ Console.live

    nextInvocation()
      .flatMap(request =>
        (lambdaEventDecoder.decodeJson(request.payload) match {
          case Left(errorMessage) =>
            ZIO.fail(new Throwable(s"Error decoding payload. Payload=${request.payload}, Error$errorMessage"))

          case Right(event) =>
            handle(event)
              .map(_.toJson)
              .flatMap(invocationResponse(request.id, _))

        }).tapError(throwable => invocationError(request.id, InvocationError.fromThrowable(throwable)))
      )
      .forever
      .tapError(throwable => initializationError(InvocationError.fromThrowable(throwable)))
      .provideCustomLayer(runtimeLayer)
      .exitCode
  }
}
