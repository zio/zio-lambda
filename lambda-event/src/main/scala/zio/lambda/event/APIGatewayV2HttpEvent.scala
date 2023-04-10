package zio.lambda.event

import zio.json._

final case class APIGatewayV2HttpEvent(
  version: String,
  routeKey: String,
  rawPath: String,
  rawQueryString: String,
  cookies: List[String],
  headers: Map[String, String],
  queryStringParameters: Map[String, String],
  pathParameters: Map[String, String],
  stageVariables: Map[String, String],
  body: String,
  isBase64Encoded: Boolean,
  requestContext: APIGatewayV2HTTPRequestContext
)

object APIGatewayV2HttpEvent {
  implicit val decoder: JsonDecoder[APIGatewayV2HttpEvent] = DeriveJsonDecoder.gen[APIGatewayV2HttpEvent]
}

final case class APIGatewayV2HTTPRequestContext(
  routeKey: String,
  accountId: String,
  stage: String,
  apiId: String,
  domainName: String,
  domainPrefix: String,
  time: String,
  timeEpoch: Long,
  http: APIGatewayV2HTTPRequestContextHttp,
  authorizer: Option[APIGatewayV2HTTPRequestContextAuthorizer],
  requestId: String
)

object APIGatewayV2HTTPRequestContext {
  implicit val decoder: JsonDecoder[APIGatewayV2HTTPRequestContext] =
    DeriveJsonDecoder.gen[APIGatewayV2HTTPRequestContext]
}

final case class APIGatewayV2HTTPRequestContextHttp(
  method: String,
  path: String,
  protocol: String,
  sourceIp: String,
  userAgent: String
)

object APIGatewayV2HTTPRequestContextHttp {
  implicit val decoder: JsonDecoder[APIGatewayV2HTTPRequestContextHttp] =
    DeriveJsonDecoder.gen[APIGatewayV2HTTPRequestContextHttp]
}

final case class APIGatewayV2HTTPRequestContextAuthorizer(
  jwt: APIGatewayV2HTTPRequestContextAuthorizerJWT,
  lambda: Map[String, String],
  iam: APIGatewayV2HTTPRequestContextAuthorizerIAM
)

object APIGatewayV2HTTPRequestContextAuthorizer {
  implicit val decoder: JsonDecoder[APIGatewayV2HTTPRequestContextAuthorizer] =
    DeriveJsonDecoder.gen[APIGatewayV2HTTPRequestContextAuthorizer]
}

final case class APIGatewayV2HTTPRequestContextAuthorizerJWT(claims: Map[String, String], scopes: List[String])

object APIGatewayV2HTTPRequestContextAuthorizerJWT {
  implicit val decoder: JsonDecoder[APIGatewayV2HTTPRequestContextAuthorizerJWT] =
    DeriveJsonDecoder.gen[APIGatewayV2HTTPRequestContextAuthorizerJWT]
}

final case class APIGatewayV2HTTPRequestContextAuthorizerIAM(
  accessKey: String,
  accountId: String,
  callerId: String,
  cognitoIdentity: APIGatewayV2HTTPRequestContextAuthorizerIAMCognitoIdentity,
  principalOrgId: String,
  userArn: String,
  userId: String
)

object APIGatewayV2HTTPRequestContextAuthorizerIAM {
  implicit val decoder: JsonDecoder[APIGatewayV2HTTPRequestContextAuthorizerIAM] =
    DeriveJsonDecoder.gen[APIGatewayV2HTTPRequestContextAuthorizerIAM]
}

final case class APIGatewayV2HTTPRequestContextAuthorizerIAMCognitoIdentity(
  amr: List[String],
  identityId: String,
  identityPoolId: String
)

object APIGatewayV2HTTPRequestContextAuthorizerIAMCognitoIdentity {
  implicit val decoder: JsonDecoder[APIGatewayV2HTTPRequestContextAuthorizerIAMCognitoIdentity] =
    DeriveJsonDecoder.gen[APIGatewayV2HTTPRequestContextAuthorizerIAMCognitoIdentity]
}
