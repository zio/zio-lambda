package zio.lambda.event

import zio.json._

/** An Amazon Lex event. A placeholder with no fields: the decoder accepts any JSON value and discards it. */
final case class LexEvent()

object LexEvent {

  /** Decodes a [[LexEvent]] from JSON. */
  implicit val decoder: JsonDecoder[LexEvent] = DeriveJsonDecoder.gen[LexEvent]
}
