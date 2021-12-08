package zio.lambda

import zio._
import zio.blocking.Blocking
import zio.json._
import zio.lambda.internal.LambdaEnvironmentLive
import zio.lambda.internal.RuntimeApiLive
import zio.lambda.internal.SttpClientLive
import zio.lambda.internal.ZRuntime

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

  def apply(event: E): RIO[ZEnv with Has[Context], A]

  def applyJson(json: String): RIO[ZEnv with Has[Context], String] =
    lambdaEventDecoder.decodeJson(json) match {
      case Left(errorMessage) =>
        ZIO.fail(new Throwable(s"Error decoding json. Json=$json, Error$errorMessage"))

      case Right(event) => apply(event).map(_.toJson)
    }

  def getContext: ZIO[Has[Context], Nothing, Context] = ZIO.service[Context]

  final override def run(args: List[String]): URIO[ZEnv, ExitCode] = {
    val runtimeApiLayer = (
      LambdaEnvironmentLive.layer ++
        Blocking.live ++
        SttpClientLive.layer
    ) >>> RuntimeApiLive.layer

    ZRuntime
      .processInvocation(Right(self))
      .forever
      .provideCustomLayer(runtimeApiLayer ++ LambdaEnvironmentLive.layer)
      .exitCode
  }

}
