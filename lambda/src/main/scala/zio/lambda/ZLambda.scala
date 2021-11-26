package zio.lambda

import zio._
import zio.lambda.internal.LambdaEnvironment
import zio.lambda.internal.SttpClient
import zio.lambda.internal.RuntimeApi
import zio.lambda.internal.ZRuntime
import zio.blocking.Blocking
import zio.json._

/**
 * Class to be extended by the Lambda's function.
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
      LambdaEnvironment.live ++
        Blocking.live ++
        SttpClient.layer
    ) >>> RuntimeApi.layer

    ZRuntime
      .processInvocation(Right(self))
      .forever
      .provideCustomLayer((runtimeApiLayer ++ LambdaEnvironment.live) >>> ZRuntime.layer)
      .exitCode
  }

  final def runHandler(json: String, context: Context): RIO[ZEnv, String] =
    lambdaEventDecoder.decodeJson(json) match {
      case Left(errorMessage) =>
        ZIO.fail(new Throwable(s"Error decoding json. Json=$json, Error$errorMessage"))

      case Right(event) => handle(event, context).map(_.toJson)
    }

}
