package zio.lambda.example

import zio._
import zio.console._
import zio.lambda.ZLambda

object SimpleHandler extends ZLambda[CustomEvent, CustomResponse] {

  override def handle(request: CustomEvent): ZIO[ZEnv, Throwable, CustomResponse] =
    for {
      _ <- putStrLn(request.message)
    } yield CustomResponse("Handler ran successfully")

}
