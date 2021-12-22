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
        check(JavaLambdaEventsGen.genScheduledEvent) { scheduledEvent =>
          assert(JavaLambdaEventJsonEncoder.toJson(scheduledEvent).fromJson[LambdaEvent.Scheduled])(isRight)
        }
      },
      testM("should decode Kafka JSON") {
        check(JavaLambdaEventsGen.genKafkaEvent) { kafkaEvent =>
          assert(JavaLambdaEventJsonEncoder.toJson(kafkaEvent).fromJson[LambdaEvent.Kafka])(isRight)
        }
      },
      testM("should decode SQS JSON") {
        check(JavaLambdaEventsGen.genSQSEvent) { sqsEvent =>
          assert(JavaLambdaEventJsonEncoder.toJson(sqsEvent).fromJson[LambdaEvent.SQS])(isRight)
        }
      }
    )

}
