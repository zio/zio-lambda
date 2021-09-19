package zio.lambda.example

import zio.test._
import zio.json.JsonDecoder
import zio.test.Assertion._

object SimpleHandlerSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[Environment, Failure] = suite("")(
    test("") {
      val jsonDecoder = implicitly[JsonDecoder[CustomEvent]]
      val test        = jsonDecoder.decodeJson("""{"message":"Hello world"}""")
      println(test)

      assert(true)(equalTo(true))
    }
  )

}
