package zio.lambda.event

import zio.json._

/** An Amazon Kinesis Data Analytics input preprocessing event for a Kinesis Data Firehose source. */
final case class KinesisAnalyticsFirehoseInputPreprocessingEvent(
  invocationId: String,
  applicationArn: String,
  streamArn: String,
  records: List[KinesisAnalyticsFirehoseInputPreprocessingRecord]
)
object KinesisAnalyticsFirehoseInputPreprocessingEvent {

  /** Decodes a [[KinesisAnalyticsFirehoseInputPreprocessingEvent]] from JSON. */
  implicit val decoder: JsonDecoder[KinesisAnalyticsFirehoseInputPreprocessingEvent] =
    DeriveJsonDecoder.gen[KinesisAnalyticsFirehoseInputPreprocessingEvent]
}

/** One record of a [[KinesisAnalyticsFirehoseInputPreprocessingEvent]]. */
final case class KinesisAnalyticsFirehoseInputPreprocessingRecord(
  recordId: String,
  kinesisFirehoseRecordMetadata: KinesisFirehoseRecordMetadata,
  data: String // Revisit this as it was defined as java.nio.ByteBuffer
)
object KinesisAnalyticsFirehoseInputPreprocessingRecord {

  /** Decodes a [[KinesisAnalyticsFirehoseInputPreprocessingRecord]] from JSON. */
  implicit val decoder: JsonDecoder[KinesisAnalyticsFirehoseInputPreprocessingRecord] =
    DeriveJsonDecoder.gen[KinesisAnalyticsFirehoseInputPreprocessingRecord]
}

/** The Kinesis Data Firehose metadata of a [[KinesisAnalyticsFirehoseInputPreprocessingRecord]]. */
final case class KinesisFirehoseRecordMetadata(approximateArrivalTimestamp: Long)
object KinesisFirehoseRecordMetadata {

  /** Decodes a [[KinesisFirehoseRecordMetadata]] from JSON. */
  implicit val decoder: JsonDecoder[KinesisFirehoseRecordMetadata] =
    DeriveJsonDecoder.gen[KinesisFirehoseRecordMetadata]
}
