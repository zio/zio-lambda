package zio.lambda.event

import zio.json._

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
  implicit val decoder: JsonDecoder[CognitoUserPoolCustomMessageEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolCustomMessageEvent]
}

final case class CognitoUserPoolCustomMessageCallerContext(awsSdkVersion: String, clientId: String)
object CognitoUserPoolCustomMessageCallerContext {
  implicit val decoder: JsonDecoder[CognitoUserPoolCustomMessageCallerContext] =
    DeriveJsonDecoder.gen[CognitoUserPoolCustomMessageCallerContext]
}

final case class CognitoUserPoolCustomMessageRequest()
object CognitoUserPoolCustomMessageRequest {
  implicit val decoder: JsonDecoder[CognitoUserPoolCustomMessageRequest] =
    DeriveJsonDecoder.gen[CognitoUserPoolCustomMessageRequest]
}

final case class CognitoUserPoolCustomMessageResponse()
object CognitoUserPoolCustomMessageResponse {
  implicit val decoder: JsonDecoder[CognitoUserPoolCustomMessageResponse] =
    DeriveJsonDecoder.gen[CognitoUserPoolCustomMessageResponse]
}
