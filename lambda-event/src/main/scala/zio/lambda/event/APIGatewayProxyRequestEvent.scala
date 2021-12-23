package zio.lambda.event

import zio.json._

final case class APIGatewayProxyRequestEvent(
  resource: String,
  path: String,
  httpMethod: String,
  headers: Map[String, String],
  multiValueHeaders: Map[String, List[String]],
  queryStringParameters: Map[String, String],
  multiValueQueryStringParameters: Map[String, List[String]],
  pathParameters: Map[String, String],
  stageVariables: Map[String, String],
  requestContext: APIGatewayProxyRequestRequestContext,
  body: String,
  isBase64Encoded: Boolean
)

object APIGatewayProxyRequest {
  implicit val decoder: JsonDecoder[APIGatewayProxyRequestEvent] = DeriveJsonDecoder.gen[APIGatewayProxyRequestEvent]
}

final case class APIGatewayProxyRequestRequestContext(
  accountId: String,
  stage: String,
  resourceId: String,
  requestId: String,
  operationName: String,
  identity: APIGatewayProxyRequestRequestContextIdentity,
  resourcePath: String,
  httpMethod: String,
  apiId: String,
  path: String,
  authorizer: Map[String, String]
)

object APIGatewayProxyRequestRequestContext {
  implicit val decoder: JsonDecoder[APIGatewayProxyRequestRequestContext] =
    DeriveJsonDecoder.gen[APIGatewayProxyRequestRequestContext]
}

final case class APIGatewayProxyRequestRequestContextIdentity(
  cognitoIdentityPoolId: String,
  accountId: String,
  cognitoIdentityId: String,
  caller: String,
  apiKey: String,
  principalOrgId: String,
  sourceIp: String,
  cognitoAuthenticationType: String,
  cognitoAuthenticationProvider: String,
  userArn: String,
  userAgent: String,
  user: String,
  accessKey: String
)

object APIGatewayProxyRequestRequestContextIdentity {
  implicit val decoder: JsonDecoder[APIGatewayProxyRequestRequestContextIdentity] =
    DeriveJsonDecoder.gen[APIGatewayProxyRequestRequestContextIdentity]
}
