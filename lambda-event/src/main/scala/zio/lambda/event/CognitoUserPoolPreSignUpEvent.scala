package zio.lambda.event

import zio.json._

/**
 * An Amazon Cognito user pool Pre Sign-up trigger event. A placeholder with no fields: the decoder accepts any JSON value and discards it.
 */
final case class CognitoUserPoolPreSignUpEvent()
object CognitoUserPoolPreSignUpEvent {

  /** Decodes a [[CognitoUserPoolPreSignUpEvent]] from JSON. */
  implicit val decoder: JsonDecoder[CognitoUserPoolPreSignUpEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolPreSignUpEvent]
}
