package zio.runtime.lambda.example

import zio._
import zio.console._
import zio.runtime.lambda.ZLambda

final class SimpleHandler extends ZLambda[CustomEvent, CustomResponse] {

  override def handle(request: CustomEvent): RIO[ZEnv, CustomResponse] =
    for {
      _ <- putStrLn(request.message)
    } yield CustomResponse("Handler ran successfully")

}
