package zio.lambda

import sttp.client3.HttpURLConnectionBackend
import zio._
import zio.blocking.Blocking
import zio.json._
import zio.runtime.LambdaEnvironment
import zio.runtime.RuntimeApi
import zio.runtime.ZRuntime

/**
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
) extends App {

  def handle(event: E): RIO[ZEnv, A]

  final override def run(args: List[String]): URIO[ZEnv, ExitCode] = {

    val runtimeApiLayer = (LambdaEnvironment.live ++
      Blocking.live ++
      ZLayer.succeed(HttpURLConnectionBackend())) >>> RuntimeApi.layer

    ZRuntime.processInvocation { json =>
      lambdaEventDecoder.decodeJson(json) match {
        case Left(errorMessage) =>
          ZIO.fail(new Throwable(s"Error decoding json. Json=$json, Error$errorMessage"))

        case Right(event) => handle(event).map(_.toJson)
      }
    }.provideCustomLayer(
      runtimeApiLayer >>> ZRuntime.layer
    ).exitCode
  }

}
