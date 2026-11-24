package zio.lambda.event

import zio.json._

/** An Amazon Kinesis Data Analytics output delivery event delivered to a Lambda function. */
final case class KinesisAnalyticsOutputDeliveryEvent(
  invocationId: String,
  applicationArn: String,
  records: List[KinesisAnalyticsOutputDeliveryRecord]
)

object KinesisAnalyticsOutputDeliveryEvent {

  /** Decodes a [[KinesisAnalyticsOutputDeliveryEvent]] from JSON. */
  implicit val decoder: JsonDecoder[KinesisAnalyticsOutputDeliveryEvent] =
    DeriveJsonDecoder.gen[KinesisAnalyticsOutputDeliveryEvent]
}

/** One record of a [[KinesisAnalyticsOutputDeliveryEvent]]. */
final case class KinesisAnalyticsOutputDeliveryRecord(
  recordId: String,
  lambdaDeliveryRecordMetadata: LambdaDeliveryRecordMetadata,
  data: String // Revisit this as it was defined as java.nio.ByteBuffer
)
object KinesisAnalyticsOutputDeliveryRecord {

  /** Decodes a [[KinesisAnalyticsOutputDeliveryRecord]] from JSON. */
  implicit val decoder: JsonDecoder[KinesisAnalyticsOutputDeliveryRecord] =
    DeriveJsonDecoder.gen[KinesisAnalyticsOutputDeliveryRecord]
}

/** The delivery metadata of a [[KinesisAnalyticsOutputDeliveryRecord]]. */
final case class LambdaDeliveryRecordMetadata(retryHint: Long)
object LambdaDeliveryRecordMetadata {

  /** Decodes a [[LambdaDeliveryRecordMetadata]] from JSON. */
  implicit val decoder: JsonDecoder[LambdaDeliveryRecordMetadata] =
    DeriveJsonDecoder.gen[LambdaDeliveryRecordMetadata]
}
