package zio.lambda.event

import zio.json._

final case class CognitoUserPoolCreateAuthChallengeEvent(
  version: String,
  triggerSource: String,
  region: String,
  userPoolId: String,
  userName: String,
  callerContext: CognitoUserPoolCreateAuthChallengeCallerContext,
  request: CognitoUserPoolCreateAuthChallengeRequest,
  response: CognitoUserPoolCreateAuthChallengeResponse
)

object CognitoUserPoolCreateAuthChallenge {
  implicit val decoder: JsonDecoder[CognitoUserPoolCreateAuthChallengeEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolCreateAuthChallengeEvent]
}

final case class CognitoUserPoolCreateAuthChallengeCallerContext(awsSdkVersion: String, clientId: String)
object CognitoUserPoolCreateAuthChallengeCallerContext {
  implicit val decoder: JsonDecoder[CognitoUserPoolCreateAuthChallengeCallerContext] =
    DeriveJsonDecoder.gen[CognitoUserPoolCreateAuthChallengeCallerContext]
}

final case class CognitoUserPoolCreateAuthChallengeRequest(
  clientMetadata: Map[String, String],
  challengeName: String,
  // session: List[ChallengeResult],
  userNotFound: Boolean
)
object CognitoUserPoolCreateAuthChallengeRequest {
  implicit val decoder: JsonDecoder[CognitoUserPoolCreateAuthChallengeRequest] =
    DeriveJsonDecoder.gen[CognitoUserPoolCreateAuthChallengeRequest]
}

final case class CognitoUserPoolCreateAuthChallengeResponse(
  publicChallengeParameters: Map[String, String],
  privateChallengeParameters: Map[String, String],
  challengeMetadata: String
)
object CognitoUserPoolCreateAuthChallengeResponse {
  implicit val decoder: JsonDecoder[CognitoUserPoolCreateAuthChallengeResponse] =
    DeriveJsonDecoder.gen[CognitoUserPoolCreateAuthChallengeResponse]
}
