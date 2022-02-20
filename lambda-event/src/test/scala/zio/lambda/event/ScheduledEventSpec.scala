package zio.lambda.event

import zio.test._
import zio.test.Assertion._
import zio.json._

object ScheduledEventSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[Environment, Failure] =
    suite("ScheduledEvent spec")(
      test("should decode Scheduled JSON") {
        check(JavaLambdaEventsGen.genScheduledEvent) { scheduledEvent =>
          assert(JavaLambdaEventJsonEncoder.toJson(scheduledEvent).fromJson[ScheduledEvent])(isRight)
        }
      }
    )
}
