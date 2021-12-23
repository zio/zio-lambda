package zio.lambda.event

import zio.json._

final case class KinesisFirehoseEvent()
object KinesisFirehoseEvent {
  implicit val decoder: JsonDecoder[KinesisFirehoseEvent] = DeriveJsonDecoder.gen[KinesisFirehoseEvent]
}
