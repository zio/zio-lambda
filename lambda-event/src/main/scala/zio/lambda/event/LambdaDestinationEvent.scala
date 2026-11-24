package zio.lambda.event

import zio.json._

/**
 * An AWS Lambda destination event. A placeholder with no fields: the decoder accepts any JSON value and discards it.
 */
final case class LambdaDestinationEvent()

object LambdaDestinationEvent {

  /** Decodes a [[LambdaDestinationEvent]] from JSON. */
  implicit val decoder: JsonDecoder[LambdaDestinationEvent] = DeriveJsonDecoder.gen[LambdaDestinationEvent]
}
