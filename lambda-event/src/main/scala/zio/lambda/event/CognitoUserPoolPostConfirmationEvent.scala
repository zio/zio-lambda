package zio.lambda.event

import zio.json._

final case class CognitoUserPoolPostConfirmationEvent()
object CognitoUserPoolPostConfirmationEvent {
  implicit val decoder: JsonDecoder[CognitoUserPoolPostConfirmationEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolPostConfirmationEvent]
}
