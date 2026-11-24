package zio.lambda.event

import zio.json._

/**
 * An Amazon Cognito user pool Post Confirmation trigger event. A placeholder with no fields: the decoder accepts any JSON value and discards it.
 */
final case class CognitoUserPoolPostConfirmationEvent()
object CognitoUserPoolPostConfirmationEvent {

  /** Decodes a [[CognitoUserPoolPostConfirmationEvent]] from JSON. */
  implicit val decoder: JsonDecoder[CognitoUserPoolPostConfirmationEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolPostConfirmationEvent]
}
