package zio.lambda.event

import zio.json._

/**
 * An Amazon Cognito user pool Define Auth Challenge trigger event. A placeholder with no fields: the decoder accepts any JSON value and discards it.
 */
final case class CognitoUserPoolDefineAuthChallengeEvent()

object CognitoUserPoolDefineAuthChallengeEvent {

  /** Decodes a [[CognitoUserPoolDefineAuthChallengeEvent]] from JSON. */
  implicit val decoder: JsonDecoder[CognitoUserPoolDefineAuthChallengeEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolDefineAuthChallengeEvent]
}
