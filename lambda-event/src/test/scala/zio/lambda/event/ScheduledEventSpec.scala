package zio.lambda.event

import zio.json._
import zio.test.Assertion._
import zio.test._

object ScheduledEventSpec extends ZIOSpecDefault {

  override def spec =
    suite("ScheduledEvent spec")(
      test("should decode Scheduled JSON") {
        check(JavaLambdaEventsGen.genScheduledEvent) { scheduledEvent =>
          assert(JavaLambdaEventJsonEncoder.toJson(scheduledEvent).fromJson[ScheduledEvent])(isRight)
        }
      }
    )
}
