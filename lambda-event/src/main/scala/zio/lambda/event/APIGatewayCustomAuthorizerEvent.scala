package zio.lambda.event

import zio.json._

/** An Amazon API Gateway REST API request delivered to a Lambda authorizer. */
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

  /** Decodes a [[APIGatewayCustomAuthorizerEvent]] from JSON. */
  implicit val decoder: JsonDecoder[APIGatewayCustomAuthorizerEvent] =
    DeriveJsonDecoder.gen[APIGatewayCustomAuthorizerEvent]
}

/** The `requestContext` of an [[APIGatewayCustomAuthorizerEvent]]. */
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

  /** Decodes a [[APIGatewayCustomAuthorizerRequestContext]] from JSON. */
  implicit val decoder: JsonDecoder[APIGatewayCustomAuthorizerRequestContext] =
    DeriveJsonDecoder.gen[APIGatewayCustomAuthorizerRequestContext]
}

/** The caller identity details in the request context of an [[APIGatewayCustomAuthorizerEvent]]. */
final case class APIGatewayCustomAuthorizerRequestContextIdentity(apiKey: String, sourceIp: String)

object APIGatewayCustomAuthorizerRequestContextIdentity {

  /** Decodes a [[APIGatewayCustomAuthorizerRequestContextIdentity]] from JSON. */
  implicit val decoder: JsonDecoder[APIGatewayCustomAuthorizerRequestContextIdentity] =
    DeriveJsonDecoder.gen[APIGatewayCustomAuthorizerRequestContextIdentity]
}
