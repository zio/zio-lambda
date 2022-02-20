package zio.lambda.event

import zio.test._
import zio.test.Assertion._
import zio.json._

object KinesisEventSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[Environment, Failure] =
    suite("KinesisEvent spec")(
      test("should decode Kinesis JSON") {
        check(JavaLambdaEventsGen.genKinesisEvent) { kinesisEvent =>
          assert(JavaLambdaEventJsonEncoder.toJson(kinesisEvent).fromJson[KinesisEvent])(isRight)
        }
      }
    )

}
