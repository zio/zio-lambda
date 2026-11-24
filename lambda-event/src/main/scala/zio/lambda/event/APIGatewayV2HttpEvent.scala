package zio.lambda.event

import zio.json._

/** An Amazon API Gateway HTTP API request (payload format 2.0) delivered to a Lambda function. */
final case class APIGatewayV2HttpEvent(
  version: String,
  routeKey: String,
  rawPath: String,
  rawQueryString: String,
  cookies: Option[List[String]],
  headers: Map[String, String],
  queryStringParameters: Option[Map[String, String]],
  pathParameters: Option[Map[String, String]],
  stageVariables: Option[Map[String, String]],
  body: String,
  isBase64Encoded: Boolean,
  requestContext: APIGatewayV2HTTPRequestContext
)

object APIGatewayV2HttpEvent {

  /** Decodes a [[APIGatewayV2HttpEvent]] from JSON. */
  implicit val decoder: JsonDecoder[APIGatewayV2HttpEvent] = DeriveJsonDecoder.gen[APIGatewayV2HttpEvent]
}

/** The `requestContext` of an [[APIGatewayV2HttpEvent]]. */
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

  /** Decodes a [[APIGatewayV2HTTPRequestContext]] from JSON. */
  implicit val decoder: JsonDecoder[APIGatewayV2HTTPRequestContext] =
    DeriveJsonDecoder.gen[APIGatewayV2HTTPRequestContext]
}

/**
 * The `http` object of an [[APIGatewayV2HTTPRequestContext]]: the request method, path, protocol, source IP and user agent.
 */
final case class APIGatewayV2HTTPRequestContextHttp(
  method: String,
  path: String,
  protocol: String,
  sourceIp: String,
  userAgent: String
)

object APIGatewayV2HTTPRequestContextHttp {

  /** Decodes a [[APIGatewayV2HTTPRequestContextHttp]] from JSON. */
  implicit val decoder: JsonDecoder[APIGatewayV2HTTPRequestContextHttp] =
    DeriveJsonDecoder.gen[APIGatewayV2HTTPRequestContextHttp]
}

/** The `authorizer` object of an [[APIGatewayV2HTTPRequestContext]]. */
final case class APIGatewayV2HTTPRequestContextAuthorizer(
  jwt: APIGatewayV2HTTPRequestContextAuthorizerJWT,
  lambda: Map[String, String],
  iam: APIGatewayV2HTTPRequestContextAuthorizerIAM
)

object APIGatewayV2HTTPRequestContextAuthorizer {

  /** Decodes a [[APIGatewayV2HTTPRequestContextAuthorizer]] from JSON. */
  implicit val decoder: JsonDecoder[APIGatewayV2HTTPRequestContextAuthorizer] =
    DeriveJsonDecoder.gen[APIGatewayV2HTTPRequestContextAuthorizer]
}

/** The JWT authorizer details of an [[APIGatewayV2HTTPRequestContextAuthorizer]]. */
final case class APIGatewayV2HTTPRequestContextAuthorizerJWT(claims: Map[String, String], scopes: List[String])

object APIGatewayV2HTTPRequestContextAuthorizerJWT {

  /** Decodes a [[APIGatewayV2HTTPRequestContextAuthorizerJWT]] from JSON. */
  implicit val decoder: JsonDecoder[APIGatewayV2HTTPRequestContextAuthorizerJWT] =
    DeriveJsonDecoder.gen[APIGatewayV2HTTPRequestContextAuthorizerJWT]
}

/** The IAM authorizer details of an [[APIGatewayV2HTTPRequestContextAuthorizer]]. */
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

  /** Decodes a [[APIGatewayV2HTTPRequestContextAuthorizerIAM]] from JSON. */
  implicit val decoder: JsonDecoder[APIGatewayV2HTTPRequestContextAuthorizerIAM] =
    DeriveJsonDecoder.gen[APIGatewayV2HTTPRequestContextAuthorizerIAM]
}

/** The Amazon Cognito identity details of an [[APIGatewayV2HTTPRequestContextAuthorizerIAM]]. */
final case class APIGatewayV2HTTPRequestContextAuthorizerIAMCognitoIdentity(
  amr: List[String],
  identityId: String,
  identityPoolId: String
)

object APIGatewayV2HTTPRequestContextAuthorizerIAMCognitoIdentity {

  /** Decodes a [[APIGatewayV2HTTPRequestContextAuthorizerIAMCognitoIdentity]] from JSON. */
  implicit val decoder: JsonDecoder[APIGatewayV2HTTPRequestContextAuthorizerIAMCognitoIdentity] =
    DeriveJsonDecoder.gen[APIGatewayV2HTTPRequestContextAuthorizerIAMCognitoIdentity]
}
