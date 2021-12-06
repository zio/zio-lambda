package zio.lambda.internal

import zio.json.DecoderOps
import zio.lambda.LambdaEvent
import zio.test.Assertion._
import zio.test._

object LambdaEventSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[Environment, Failure] =
    suite("LambdaEvent spec")(
      testM("should decode Kinesis JSON") {
        check(JavaLambdaEventsGen.genKinesisEvent) { kinesisEvent =>
          assert(JavaLambdaEventJsonEncoder.toJson(kinesisEvent).fromJson[LambdaEvent.Kinesis])(isRight)
        }
      },
      testM("should decode Scheduled JSON") {
        check(JavaLambdaEventsGen.genScheduled) { scheduledEvent =>
          assert(JavaLambdaEventJsonEncoder.toJson(scheduledEvent).fromJson[LambdaEvent.Scheduled])(isRight)
        }
      }
    )

}
