package zio.lambda.event

import zio.json._
import zio.test.Assertion._
import zio.test._

object SQSEventSpec extends ZIOSpecDefault {

  override def spec =
    suite("SQSEvent spec")(
      test("should decode SQS JSON") {
        check(JavaLambdaEventsGen.genSQSEvent) { sqsEvent =>
          assert(JavaLambdaEventJsonEncoder.toJson(sqsEvent).fromJson[SQSEvent])(isRight)
        }
      }
    )

}
