package zio.lambda.event

import zio.json._

final case class CognitoUserPoolPreAuthenticationEvent()
object CognitoUserPoolPreAuthentication {
  implicit val decoder: JsonDecoder[CognitoUserPoolPreAuthenticationEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolPreAuthenticationEvent]
}
