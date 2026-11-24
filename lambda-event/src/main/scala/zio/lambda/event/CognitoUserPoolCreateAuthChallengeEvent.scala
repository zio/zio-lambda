package zio.lambda.event

import zio.json._

/** An Amazon Cognito user pool Create Auth Challenge trigger event. */
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

/** Holds the JSON decoder for [[CognitoUserPoolCreateAuthChallengeEvent]]. */
object CognitoUserPoolCreateAuthChallenge {

  /** Decodes a [[CognitoUserPoolCreateAuthChallengeEvent]] from JSON. */
  implicit val decoder: JsonDecoder[CognitoUserPoolCreateAuthChallengeEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolCreateAuthChallengeEvent]
}

/** The `callerContext` of a [[CognitoUserPoolCreateAuthChallengeEvent]]. */
final case class CognitoUserPoolCreateAuthChallengeCallerContext(awsSdkVersion: String, clientId: String)
object CognitoUserPoolCreateAuthChallengeCallerContext {

  /** Decodes a [[CognitoUserPoolCreateAuthChallengeCallerContext]] from JSON. */
  implicit val decoder: JsonDecoder[CognitoUserPoolCreateAuthChallengeCallerContext] =
    DeriveJsonDecoder.gen[CognitoUserPoolCreateAuthChallengeCallerContext]
}

/** The `request` of a [[CognitoUserPoolCreateAuthChallengeEvent]]. */
final case class CognitoUserPoolCreateAuthChallengeRequest(
  clientMetadata: Map[String, String],
  challengeName: String,
  // session: List[ChallengeResult],
  userNotFound: Boolean
)
object CognitoUserPoolCreateAuthChallengeRequest {

  /** Decodes a [[CognitoUserPoolCreateAuthChallengeRequest]] from JSON. */
  implicit val decoder: JsonDecoder[CognitoUserPoolCreateAuthChallengeRequest] =
    DeriveJsonDecoder.gen[CognitoUserPoolCreateAuthChallengeRequest]
}

/** The `response` of a [[CognitoUserPoolCreateAuthChallengeEvent]]. */
final case class CognitoUserPoolCreateAuthChallengeResponse(
  publicChallengeParameters: Map[String, String],
  privateChallengeParameters: Map[String, String],
  challengeMetadata: String
)
object CognitoUserPoolCreateAuthChallengeResponse {

  /** Decodes a [[CognitoUserPoolCreateAuthChallengeResponse]] from JSON. */
  implicit val decoder: JsonDecoder[CognitoUserPoolCreateAuthChallengeResponse] =
    DeriveJsonDecoder.gen[CognitoUserPoolCreateAuthChallengeResponse]
}
