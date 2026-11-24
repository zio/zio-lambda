package zio.lambda.event

import zio.json._

/**
 * An Amazon SNS notification event. A placeholder with no fields: the decoder accepts any JSON value and discards it.
 */
final case class SNSEvent()
object SNSEvent {

  /** Decodes a [[SNSEvent]] from JSON. */
  implicit val decoder: JsonDecoder[SNSEvent] = DeriveJsonDecoder.gen[SNSEvent]
}
