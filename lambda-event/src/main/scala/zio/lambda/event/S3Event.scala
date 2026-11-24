package zio.lambda.event

import zio.json._

/**
 * An Amazon S3 event notification. A placeholder with no fields: the decoder accepts any JSON value and discards it.
 */
final case class S3Event()
object S3Event {

  /** Decodes a [[S3Event]] from JSON. */
  implicit val decoder: JsonDecoder[S3Event] = DeriveJsonDecoder.gen[S3Event]
}
