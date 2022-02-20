package zio.lambda.example

import zio._
import zio.Console._
import zio.lambda.Context
import zio.lambda.ZLambdaApp

object SimpleHandler extends ZLambdaApp[CustomEvent, CustomResponse] {

  override def apply(event: CustomEvent, context: Context): RIO[ZEnv, CustomResponse] =
    for {
      _ <- printLine(event.message)
    } yield CustomResponse("Handler ran successfully")

}
