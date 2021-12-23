package zio.lambda.event

import zio.json._

final case class SecretsManagerRotationEvent()
object SecretsManagerRotationEvent {
  implicit val decoder: JsonDecoder[SecretsManagerRotationEvent] = DeriveJsonDecoder.gen[SecretsManagerRotationEvent]
}
