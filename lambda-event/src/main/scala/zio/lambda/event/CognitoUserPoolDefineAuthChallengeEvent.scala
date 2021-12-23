package zio.lambda.event

import zio.json._

final case class CognitoUserPoolDefineAuthChallengeEvent()

object CognitoUserPoolDefineAuthChallengeEvent {
  implicit val decoder: JsonDecoder[CognitoUserPoolDefineAuthChallengeEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolDefineAuthChallengeEvent]
}
