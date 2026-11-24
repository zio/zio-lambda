package zio.lambda.event

import zio.json._

/**
 * An Amazon Kinesis Data Analytics input preprocessing event for a Kinesis Data Streams source. A placeholder with no fields: the decoder accepts any JSON value and discards it.
 */
final case class KinesisAnalyticsStreamsInputPreprocessingEvent()
object KinesisAnalyticsStreamsInputPreprocessingEvent {

  /** Decodes a [[KinesisAnalyticsStreamsInputPreprocessingEvent]] from JSON. */
  implicit val decoder: JsonDecoder[KinesisAnalyticsStreamsInputPreprocessingEvent] =
    DeriveJsonDecoder.gen[KinesisAnalyticsStreamsInputPreprocessingEvent]
}
