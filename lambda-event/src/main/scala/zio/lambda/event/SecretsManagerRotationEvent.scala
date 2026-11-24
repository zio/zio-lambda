package zio.lambda.event

import zio.json._

/**
 * An AWS Secrets Manager rotation event. A placeholder with no fields: the decoder accepts any JSON value and discards it.
 */
final case class SecretsManagerRotationEvent()
object SecretsManagerRotationEvent {

  /** Decodes a [[SecretsManagerRotationEvent]] from JSON. */
  implicit val decoder: JsonDecoder[SecretsManagerRotationEvent] = DeriveJsonDecoder.gen[SecretsManagerRotationEvent]
}
