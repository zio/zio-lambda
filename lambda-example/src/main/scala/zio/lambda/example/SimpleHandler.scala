package zio.lambda.example

import zio.Console._
import zio._
import zio.lambda._

object SimpleHandler extends ZIOAppDefault {

  val app = ZLambdaApp { (event: CustomEvent, _: Context) =>
    for {
      _ <- printLine(event.message)
    } yield "Handler ran successfully"
  }

  override val run =
    ZLambdaRunner.serve(app).provide(ZLambdaRunner.default)
}
