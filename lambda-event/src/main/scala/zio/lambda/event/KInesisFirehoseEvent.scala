package zio.lambda.event

import zio.json._

/**
 * An Amazon Kinesis Data Firehose event. A placeholder with no fields: the decoder accepts any JSON value and discards it.
 */
final case class KinesisFirehoseEvent()
object KinesisFirehoseEvent {

  /** Decodes a [[KinesisFirehoseEvent]] from JSON. */
  implicit val decoder: JsonDecoder[KinesisFirehoseEvent] = DeriveJsonDecoder.gen[KinesisFirehoseEvent]
}
