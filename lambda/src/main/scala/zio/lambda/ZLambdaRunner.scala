package zio.lambda

import zio.json.{JsonDecoder, JsonEncoder}
import zio.json._
import zio._
import zio.lambda.internal.{LambdaEnvironment, LoopProcessor, RuntimeApiLive}

object ZLambdaRunner {

  def serve[R, IN: JsonDecoder, OUT: JsonEncoder, ERR <: Throwable](
    appFunction: (IN, Context) => ZIO[R, ERR, OUT]
  ): RIO[R, Unit] =
    (for {
      lp  <- ZIO.service[LoopProcessor]
      res <- lp.loopZioApp(Right(defaultRaw[R, IN, OUT, ERR](_, _, appFunction)))
    } yield res).provideSomeLayer[R](ZLambdaRunner.default)

  def serveRaw[R](rawFunction: (String, Context) => ZIO[R, Throwable, String]): RIO[R with LoopProcessor, Unit] =
    (for {
      lp  <- ZIO.service[LoopProcessor]
      res <- lp.loopZioApp(Right(rawFunction))
    } yield res).provideSomeLayer[R](ZLambdaRunner.default)

  private def defaultRaw[R, IN: JsonDecoder, OUT: JsonEncoder, ERR <: Throwable](
    json: String,
    context: Context,
    userFunction: (IN, Context) => ZIO[R, ERR, OUT]
  ): ZIO[R, Throwable, String] =
    JsonDecoder[IN].decodeJson(json) match {
      case Left(errorMessage) =>
        ZIO.fail(new Throwable(s"Error decoding json. Json=$json, Error$errorMessage"))
      case Right(event) =>
        userFunction(event, context).map(_.toJson)
    }

  def default =
    LambdaEnvironment.live >>> (RuntimeApiLive.layer ++ LambdaEnvironment.live) >>> LoopProcessor.live

}
