package zio.lambda.event

import zio.json._

final case class KinesisAnalyticsOutputDeliveryEvent(
  invocationId: String,
  applicationArn: String,
  records: List[KinesisAnalyticsOutputDeliveryRecord]
)

object KinesisAnalyticsOutputDeliveryEvent {
  implicit val decoder: JsonDecoder[KinesisAnalyticsOutputDeliveryEvent] =
    DeriveJsonDecoder.gen[KinesisAnalyticsOutputDeliveryEvent]
}

final case class KinesisAnalyticsOutputDeliveryRecord(
  recordId: String,
  lambdaDeliveryRecordMetadata: LambdaDeliveryRecordMetadata,
  data: String // Revisit this as it was defined as java.nio.ByteBuffer
)
object KinesisAnalyticsOutputDeliveryRecord {
  implicit val decoder: JsonDecoder[KinesisAnalyticsOutputDeliveryRecord] =
    DeriveJsonDecoder.gen[KinesisAnalyticsOutputDeliveryRecord]
}
final case class LambdaDeliveryRecordMetadata(retryHint: Long)
object LambdaDeliveryRecordMetadata {
  implicit val decoder: JsonDecoder[LambdaDeliveryRecordMetadata] =
    DeriveJsonDecoder.gen[LambdaDeliveryRecordMetadata]
}
