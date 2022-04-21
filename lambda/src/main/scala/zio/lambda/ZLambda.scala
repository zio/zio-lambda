package zio.lambda

import zio._
import zio.json._
import zio.lambda.internal.LambdaEnvironment
import zio.lambda.internal.LoopProcessor
import zio.lambda.internal.RuntimeApiLive

abstract class ZLambda[E, A](
  implicit val lambdaEventDecoder: JsonDecoder[E],
  implicit val lambdaResponseEncoder: JsonEncoder[A]
) extends ZIOAppDefault { self =>

  def apply(event: E, context: Context): Task[A]

  def applyJson(json: String, context: Context): Task[String] =
    lambdaEventDecoder.decodeJson(json) match {
      case Left(errorMessage) =>
        ZIO.fail(new Throwable(s"Error decoding json. Json=$json, Error$errorMessage"))

      case Right(event) => apply(event, context).map(_.toJson)
    }

  def run =
    LoopProcessor
      .loop(Right(self))
      .provide(
        LambdaEnvironment.live,
        RuntimeApiLive.layer,
        LoopProcessor.live
      )

}
