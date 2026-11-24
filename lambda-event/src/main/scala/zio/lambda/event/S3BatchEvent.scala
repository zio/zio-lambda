package zio.lambda.event

import zio.json._

/**
 * An Amazon S3 Batch Operations event. A placeholder with no fields: the decoder accepts any JSON value and discards it.
 */
final case class S3BatchEvent()
object S3BatchEvent {

  /** Decodes a [[S3BatchEvent]] from JSON. */
  implicit val decoder: JsonDecoder[S3BatchEvent] = DeriveJsonDecoder.gen[S3BatchEvent]
}
