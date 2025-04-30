package zio.lambda.response

import zio.json._

final case class APIGatewayV2HTTPResponse(
  body: String,
  statusCode: Int = 200,
  headers: Map[String, String] = Map(),
  multiValueHeaders: Map[String, List[String]] = Map(),
  cookies: List[String] = List(),
  isBase64Encoded: Boolean = false
)

object APIGatewayV2HTTPResponse {
  implicit val encoder: JsonEncoder[APIGatewayV2HTTPResponse] = DeriveJsonEncoder.gen[APIGatewayV2HTTPResponse]
}
