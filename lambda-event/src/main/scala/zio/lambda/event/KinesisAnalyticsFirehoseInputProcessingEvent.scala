package zio.lambda.event

import zio.json._

final case class KinesisAnalyticsFirehoseInputPreprocessingEvent(
  invocationId: String,
  applicationArn: String,
  streamArn: String,
  records: List[KinesisAnalyticsFirehoseInputPreprocessingRecord]
)
object KinesisAnalyticsFirehoseInputPreprocessingEvent {
  implicit val decoder: JsonDecoder[KinesisAnalyticsFirehoseInputPreprocessingEvent] =
    DeriveJsonDecoder.gen[KinesisAnalyticsFirehoseInputPreprocessingEvent]
}

final case class KinesisAnalyticsFirehoseInputPreprocessingRecord(
  recordId: String,
  kinesisFirehoseRecordMetadata: KinesisFirehoseRecordMetadata,
  data: String // Revisit this as it was defined as java.nio.ByteBuffer
)
object KinesisAnalyticsFirehoseInputPreprocessingRecord {
  implicit val decoder: JsonDecoder[KinesisAnalyticsFirehoseInputPreprocessingRecord] =
    DeriveJsonDecoder.gen[KinesisAnalyticsFirehoseInputPreprocessingRecord]
}
final case class KinesisFirehoseRecordMetadata(approximateArrivalTimestamp: Long)
object KinesisFirehoseRecordMetadata {
  implicit val decoder: JsonDecoder[KinesisFirehoseRecordMetadata] =
    DeriveJsonDecoder.gen[KinesisFirehoseRecordMetadata]
}
