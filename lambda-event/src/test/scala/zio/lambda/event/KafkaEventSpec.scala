package zio.lambda.event

import zio.json._
import zio.test.Assertion._
import zio.test._

object KafkaEventSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[Environment, Failure] =
    suite("KafkaEvent spec")(
      testM("should decode Kafka JSON") {
        check(JavaLambdaEventsGen.genKafkaEvent) { kafkaEvent =>
          assert(JavaLambdaEventJsonEncoder.toJson(kafkaEvent).fromJson[KafkaEvent])(isRight)
        }
      }
    )

}
