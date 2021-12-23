package zio.lambda.event

import zio.json._

final case class KinesisAnalyticsStreamsInputPreprocessingEvent()
object KinesisAnalyticsStreamsInputPreprocessingEvent {
  implicit val decoder: JsonDecoder[KinesisAnalyticsStreamsInputPreprocessingEvent] =
    DeriveJsonDecoder.gen[KinesisAnalyticsStreamsInputPreprocessingEvent]
}
