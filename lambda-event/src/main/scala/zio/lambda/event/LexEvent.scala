package zio.lambda.event

import zio.json._

final case class LexEvent()

object LexEvent {
  implicit val decoder: JsonDecoder[LexEvent] = DeriveJsonDecoder.gen[LexEvent]
}
