package zio.lambda.event

import zio.json._

/** An Amazon Cognito user pool Custom Message trigger event. */
final case class CognitoUserPoolCustomMessageEvent(
  version: String,
  triggerSource: String,
  region: String,
  userPoolId: String,
  userName: String,
  callerContext: CognitoUserPoolCustomMessageCallerContext,
  request: CognitoUserPoolCustomMessageRequest,
  response: CognitoUserPoolCustomMessageResponse
)

object CognitoUserPoolCustomMessageEvent {

  /** Decodes a [[CognitoUserPoolCustomMessageEvent]] from JSON. */
  implicit val decoder: JsonDecoder[CognitoUserPoolCustomMessageEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolCustomMessageEvent]
}

/** The `callerContext` of a [[CognitoUserPoolCustomMessageEvent]]. */
final case class CognitoUserPoolCustomMessageCallerContext(awsSdkVersion: String, clientId: String)
object CognitoUserPoolCustomMessageCallerContext {

  /** Decodes a [[CognitoUserPoolCustomMessageCallerContext]] from JSON. */
  implicit val decoder: JsonDecoder[CognitoUserPoolCustomMessageCallerContext] =
    DeriveJsonDecoder.gen[CognitoUserPoolCustomMessageCallerContext]
}

/** The `request` of a [[CognitoUserPoolCustomMessageEvent]]. It has no fields. */
final case class CognitoUserPoolCustomMessageRequest()
object CognitoUserPoolCustomMessageRequest {

  /** Decodes a [[CognitoUserPoolCustomMessageRequest]] from JSON. */
  implicit val decoder: JsonDecoder[CognitoUserPoolCustomMessageRequest] =
    DeriveJsonDecoder.gen[CognitoUserPoolCustomMessageRequest]
}

/** The `response` of a [[CognitoUserPoolCustomMessageEvent]]. It has no fields. */
final case class CognitoUserPoolCustomMessageResponse()
object CognitoUserPoolCustomMessageResponse {

  /** Decodes a [[CognitoUserPoolCustomMessageResponse]] from JSON. */
  implicit val decoder: JsonDecoder[CognitoUserPoolCustomMessageResponse] =
    DeriveJsonDecoder.gen[CognitoUserPoolCustomMessageResponse]
}
