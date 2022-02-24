package zio.lambda.example

import zio.Console._
import zio._
import zio.lambda._

object SimpleHandler extends ZLambda[CustomEvent, String] {

  override def apply(event: CustomEvent, context: Context): RIO[ZEnv, String] =
    for {
      _ <- printLine(event.message)
    } yield "Handler ran successfully"

}
