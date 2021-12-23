package zio.lambda.event

import zio.json._

final case class APIGatewayV2WebSocketEvent(
  resource: String,
  path: String,
  httpMethod: String,
  headers: Map[String, String],
  multiValueHeaders: Map[String, List[String]],
  queryStringParameters: Map[String, String],
  multiValueQueryStringParameters: Map[String, List[String]],
  pathParameters: Map[String, String],
  stageVariables: Map[String, String],
  requestContext: APIGatewayV2WebSocketRequestContext,
  body: String,
  isBase64Encoded: Boolean
)

object APIGatewayV2WebSocketEvent {
  implicit val decoder: JsonDecoder[APIGatewayV2WebSocketEvent] = DeriveJsonDecoder.gen[APIGatewayV2WebSocketEvent]
}

final case class APIGatewayV2WebSocketRequestContext(
  accountId: String,
  resourceId: String,
  stage: String,
  requestId: String,
  identity: APIGatewayV2WebSocketRequestContextIdentity,
  resourcePath: String,
  authorizer: Map[String, String],
  httpMethod: String,
  apiId: String,
  connectedAt: Long,
  connectionId: String,
  domainName: String,
  error: String,
  eventType: String,
  extendedRequestId: String,
  integrationLatency: String,
  messageDirection: String,
  messageId: String,
  requestTime: String,
  requestTimeEpoch: Long,
  routeKey: String,
  status: String
)
object APIGatewayV2WebSocketRequestContext {
  implicit val decoder: JsonDecoder[APIGatewayV2WebSocketRequestContext] =
    DeriveJsonDecoder.gen[APIGatewayV2WebSocketRequestContext]
}

final case class APIGatewayV2WebSocketRequestContextIdentity(
  cognitoIdentityPoolId: String,
  accountId: String,
  cognitoIdentityId: String,
  caller: String,
  apiKey: String,
  sourceIp: String,
  cognitoAuthenticationType: String,
  cognitoAuthenticationProvider: String,
  userArn: String,
  userAgent: String,
  user: String,
  accessKey: String
)
object APIGatewayV2WebSocketRequestContextIdentity {
  implicit val decoder: JsonDecoder[APIGatewayV2WebSocketRequestContextIdentity] =
    DeriveJsonDecoder.gen[APIGatewayV2WebSocketRequestContextIdentity]
}
