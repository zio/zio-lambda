package zio.lambda

import zio._
import zio.blocking.Blocking
import zio.json._
import zio.lambda.internal.LambdaEnvironmentLive
import zio.lambda.internal.RuntimeApiLive
import zio.lambda.internal.SttpClientLive
import zio.lambda.internal.ZRuntime
import zio.lambda.internal.ZRuntimeLive

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
abstract class ZLambda[E, A](
  implicit val lambdaEventDecoder: JsonDecoder[E],
  implicit val lambdaResponseEncoder: JsonEncoder[A]
) extends App { self =>

  def handle(event: E, context: Context): RIO[ZEnv, A]

  final override def run(args: List[String]): URIO[ZEnv, ExitCode] = {
    val runtimeApiLayer = (
      LambdaEnvironmentLive.layer ++
        Blocking.live ++
        SttpClientLive.layer
    ) >>> RuntimeApiLive.layer

    ZRuntime
      .processInvocation(Right(self))
      .forever
      .provideCustomLayer((runtimeApiLayer ++ LambdaEnvironmentLive.layer) >>> ZRuntimeLive.layer)
      .exitCode
  }

  final def runHandler(json: String, context: Context): RIO[ZEnv, String] =
    lambdaEventDecoder.decodeJson(json) match {
      case Left(errorMessage) =>
        ZIO.fail(new Throwable(s"Error decoding json. Json=$json, Error$errorMessage"))

      case Right(event) => handle(event, context).map(_.toJson)
    }

}
