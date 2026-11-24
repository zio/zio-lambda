package zio.lambda.event

import zio.json._

/**
 * An Amazon Cognito user pool Pre Token Generation trigger event. A placeholder with no fields: the decoder accepts any JSON value and discards it.
 */
final case class CognitoUserPoolPreTokenGenerationEvent()
object CognitoUserPoolPreTokenGenerationEvent {

  /** Decodes a [[CognitoUserPoolPreTokenGenerationEvent]] from JSON. */
  implicit val decoder: JsonDecoder[CognitoUserPoolPreTokenGenerationEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolPreTokenGenerationEvent]
}
