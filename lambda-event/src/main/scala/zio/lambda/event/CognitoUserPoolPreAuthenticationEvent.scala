package zio.lambda.event

import zio.json._

/**
 * An Amazon Cognito user pool Pre Authentication trigger event. A placeholder with no fields: the decoder accepts any JSON value and discards it.
 */
final case class CognitoUserPoolPreAuthenticationEvent()
object CognitoUserPoolPreAuthentication {

  /** Decodes a [[CognitoUserPoolPreAuthenticationEvent]] from JSON. */
  implicit val decoder: JsonDecoder[CognitoUserPoolPreAuthenticationEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolPreAuthenticationEvent]
}
