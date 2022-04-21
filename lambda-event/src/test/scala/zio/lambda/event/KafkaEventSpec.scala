package zio.lambda.event

import zio.json._
import zio.test.Assertion._
import zio.test._

object KafkaEventSpec extends ZIOSpecDefault {

  override def spec =
    suite("KafkaEvent spec")(
      test("should decode Kafka JSON") {
        check(JavaLambdaEventsGen.genKafkaEvent) { kafkaEvent =>
          assert(JavaLambdaEventJsonEncoder.toJson(kafkaEvent).fromJson[KafkaEvent])(isRight)
        }
      }
    )

}
