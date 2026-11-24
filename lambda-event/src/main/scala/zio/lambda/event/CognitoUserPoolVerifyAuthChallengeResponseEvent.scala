package zio.lambda.event

import zio.json._

/**
 * An Amazon Cognito user pool Verify Auth Challenge Response trigger event. A placeholder with no fields: the decoder accepts any JSON value and discards it.
 */
final case class CognitoUserPoolVerifyAuthChallengeResponseEvent()
object CognitoUserPoolVerifyAuthChallengeResponseEvent {

  /** Decodes a [[CognitoUserPoolVerifyAuthChallengeResponseEvent]] from JSON. */
  implicit val decoder: JsonDecoder[CognitoUserPoolVerifyAuthChallengeResponseEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolVerifyAuthChallengeResponseEvent]
}
