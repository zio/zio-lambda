package zio.lambda.response

import zio.json._

final case class APIGatewayV2HTTPResponse(
  statusCode: Int,
  headers: Map[String, String],
  multiValueHeaders: Map[String, List[String]],
  cookies: List[String],
  body: String,
  isBase64Encoded: Boolean
)

object APIGatewayV2HTTPResponse {
  implicit val encoder: JsonEncoder[APIGatewayV2HTTPResponse] = DeriveJsonEncoder.gen[APIGatewayV2HTTPResponse]
}
