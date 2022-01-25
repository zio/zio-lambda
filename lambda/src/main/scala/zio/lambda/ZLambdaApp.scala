package zio.lambda

import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.json._
import zio.lambda.internal.LambdaEnvironment
import zio.lambda.internal.LoopProcessor
import zio.lambda.internal.RuntimeApiLive

/**
 * Class to be extended by the Lambda function.
 *
 * Implementation example:
 *
 * {{{
 * object MyLambda extends ZLambda[MyPayload, MyResponse] {
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
abstract class ZLambdaApp[E, A](
  implicit val lambdaEventDecoder: JsonDecoder[E],
  implicit val lambdaResponseEncoder: JsonEncoder[A]
) extends App { self =>

  def apply(event: E, context: Context): RIO[ZEnv, A]

  def applyJson(json: String, context: Context): RIO[ZEnv, String] =
    lambdaEventDecoder.decodeJson(json) match {
      case Left(errorMessage) =>
        ZIO.fail(new Throwable(s"Error decoding json. Json=$json, Error$errorMessage"))

      case Right(event) => apply(event, context).map(_.toJson)
    }

  def getContext: ZIO[Has[Context], Nothing, Context] = ZIO.service[Context]

  final override def run(args: List[String]): URIO[ZEnv, ExitCode] = {
    val runtimeApiLayer = (
      LambdaEnvironment.live ++ Clock.live ++ Blocking.live
    ) >>> RuntimeApiLive.layer

    val zRuntimeLayer = (runtimeApiLayer ++ LambdaEnvironment.live) >>> LoopProcessor.live

    LoopProcessor
      .loop(Right(self))
      .provideCustomLayer(zRuntimeLayer)
      .exitCode
  }

}
