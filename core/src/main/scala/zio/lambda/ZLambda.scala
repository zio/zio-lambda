package zio.lambda

import zio._
import zio.console._

object ZLambda extends App {
  override def run(args: List[String]) =
    (for {
      _ <- putStrLn("Hello, welcome to ZIO Lambda!")
    } yield ()).exitCode
}
