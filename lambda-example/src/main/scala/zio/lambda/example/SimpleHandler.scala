package zio.lambda.example

import zio._
import zio.console._
import zio.lambda.Context
import zio.lambda.ZLambdaApp

object SimpleHandler extends ZLambdaApp[CustomEvent, CustomResponse] {

  override def apply(request: CustomEvent): RIO[ZEnv with Has[Context], CustomResponse] =
    for {
      _ <- putStrLn(request.message)
    } yield CustomResponse("Handler ran successfully")

}
