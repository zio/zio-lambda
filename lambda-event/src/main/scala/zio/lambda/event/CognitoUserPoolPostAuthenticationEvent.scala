package zio.lambda.event

import zio.json._

final case class CognitoUserPoolPostAuthenticationEvent()
object CognitoUserPoolPostAuthenticationEvent {
  implicit val decoder: JsonDecoder[CognitoUserPoolPostAuthenticationEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolPostAuthenticationEvent]
}
