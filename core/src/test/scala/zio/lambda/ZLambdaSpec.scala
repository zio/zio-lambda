package zio.lambda

import zio.test._
import zio.test.Assertion._
import zio.test.environment._

object ZLambdaSpec extends DefaultRunnableSpec {
  def spec =
    suite("ZLambdaSpec")(
      testM("should print welcome message") {
        for {
          _      <- ZLambda.run(Nil)
          output <- TestConsole.output
        } yield assert(output)(equalTo(Vector("Hello, welcome to ZIO Lambda!\n")))
      }
    )
}
