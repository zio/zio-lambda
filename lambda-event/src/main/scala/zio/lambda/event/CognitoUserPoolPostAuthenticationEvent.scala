package zio.lambda.event

import zio.json._

/**
 * An Amazon Cognito user pool Post Authentication trigger event. A placeholder with no fields: the decoder accepts any JSON value and discards it.
 */
final case class CognitoUserPoolPostAuthenticationEvent()
object CognitoUserPoolPostAuthenticationEvent {

  /** Decodes a [[CognitoUserPoolPostAuthenticationEvent]] from JSON. */
  implicit val decoder: JsonDecoder[CognitoUserPoolPostAuthenticationEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolPostAuthenticationEvent]
}
