package zio.lambda.event

import zio.json._

/**
 * An Amazon Cognito user pool trigger event. A placeholder with no fields: the decoder accepts any JSON value and discards it.
 */
final case class CognitoUserPoolEvent()

object CognitoUserPoolEvent {

  /** Decodes a [[CognitoUserPoolEvent]] from JSON. */
  implicit val decoder: JsonDecoder[CognitoUserPoolEvent] = DeriveJsonDecoder.gen[CognitoUserPoolEvent]
}
