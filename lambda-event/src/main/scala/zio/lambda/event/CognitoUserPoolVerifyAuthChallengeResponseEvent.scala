package zio.lambda.event

import zio.json._

final case class CognitoUserPoolVerifyAuthChallengeResponseEvent()
object CognitoUserPoolVerifyAuthChallengeResponseEvent {
  implicit val decoder: JsonDecoder[CognitoUserPoolVerifyAuthChallengeResponseEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolVerifyAuthChallengeResponseEvent]
}
