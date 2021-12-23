package zio.lambda.event

import zio.json._
import java.time.format.DateTimeFormatter
import java.time.OffsetDateTime

final case class APIGatewayV2CustomAuthorizerEvent(
  version: String,
  `type`: String,
  routeArn: String,
  identitySource: List[String],
  routeKey: String,
  rawPath: String,
  rawQueryString: String,
  cookies: List[String],
  headers: Map[String, String],
  queryStringParameters: Map[String, String],
  requestContext: APIGatewayV2CustomAuthorizerRequestContext,
  pathParameters: Map[String, String],
  stageVariables: Map[String, String]
)

object APIGatewayV2CustomAuthorizer {
  implicit val decoder: JsonDecoder[APIGatewayV2CustomAuthorizerEvent] =
    DeriveJsonDecoder.gen[APIGatewayV2CustomAuthorizerEvent]
}

final case class APIGatewayV2CustomAuthorizerRequestContext(
  accountId: String,
  apiId: String,
  domainName: String,
  domainPrefix: String,
  http: Http,
  requestId: String,
  routeKey: String,
  stage: String,
  time: java.time.OffsetDateTime,
  timeEpoch: Long
)

object APIGatewayV2CustomAuthorizerRequestContext {
  lazy val timeDateTimeFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z")

  implicit val timeDecoder: JsonDecoder[OffsetDateTime] =
    JsonDecoder[String].map(OffsetDateTime.parse(_, timeDateTimeFormatter))

  implicit val decoder: JsonDecoder[APIGatewayV2CustomAuthorizerRequestContext] =
    DeriveJsonDecoder.gen[APIGatewayV2CustomAuthorizerRequestContext]
}

final case class Http(
  method: String,
  path: String,
  protocol: String,
  sourceIp: String,
  userAgent: String
)

object Http {
  implicit val decoder: JsonDecoder[Http] = DeriveJsonDecoder.gen[Http]
}
