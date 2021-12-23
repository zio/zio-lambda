package zio.lambda.event

import zio.json._

final case class APIGatewayCustomAuthorizerEvent(
  version: String,
  `type`: String,
  methodArn: String,
  identitySource: String,
  authorizationToken: String,
  resource: String,
  path: String,
  httpMethod: String,
  headers: Map[String, String],
  queryStringParameters: Map[String, String],
  pathParameters: Map[String, String],
  stageVariables: Map[String, String],
  requestContext: APIGatewayCustomAuthorizerRequestContext
)

object APIGatewayCustomAuthorizerEvent {
  implicit val decoder: JsonDecoder[APIGatewayCustomAuthorizerEvent] =
    DeriveJsonDecoder.gen[APIGatewayCustomAuthorizerEvent]
}

final case class APIGatewayCustomAuthorizerRequestContext(
  path: String,
  accountId: String,
  resourceId: String,
  stage: String,
  requestId: String,
  identity: APIGatewayCustomAuthorizerRequestContextIdentity,
  resourcePath: String,
  httpMethod: String,
  apiId: String
)

object APIGatewayCustomAuthorizerRequestContext {
  implicit val decoder: JsonDecoder[APIGatewayCustomAuthorizerRequestContext] =
    DeriveJsonDecoder.gen[APIGatewayCustomAuthorizerRequestContext]
}

final case class APIGatewayCustomAuthorizerRequestContextIdentity(apiKey: String, sourceIp: String)

object APIGatewayCustomAuthorizerRequestContextIdentity {
  implicit val decoder: JsonDecoder[APIGatewayCustomAuthorizerRequestContextIdentity] =
    DeriveJsonDecoder.gen[APIGatewayCustomAuthorizerRequestContextIdentity]
}
