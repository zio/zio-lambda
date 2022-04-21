package zio.lambda.event

import zio.json._
import zio.test.Assertion._
import zio.test._

object KinesisEventSpec extends ZIOSpecDefault {

  override def spec =
    suite("KinesisEvent spec")(
      test("should decode Kinesis JSON") {
        check(JavaLambdaEventsGen.genKinesisEvent) { kinesisEvent =>
          assert(JavaLambdaEventJsonEncoder.toJson(kinesisEvent).fromJson[KinesisEvent])(isRight)
        }
      }
    )

}
