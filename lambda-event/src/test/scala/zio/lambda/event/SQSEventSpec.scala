package zio.lambda.event

import zio.test._
import zio.test.Assertion._
import zio.json._

object SQSEventSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[Environment, Failure] =
    suite("SQSEvent spec")(
      testM("should decode SQS JSON") {
        check(JavaLambdaEventsGen.genSQSEvent) { sqsEvent =>
          assert(JavaLambdaEventJsonEncoder.toJson(sqsEvent).fromJson[SQSEvent])(isRight)
        }
      }
    )

}
