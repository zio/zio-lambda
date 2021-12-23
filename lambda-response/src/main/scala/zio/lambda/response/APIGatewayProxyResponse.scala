package zio.lambda.response

import zio.json._

final case class APIGatewayProxyResponse(
  statusCode: Int,
  headers: Map[String, String],
  multiValueHeaders: Map[String, List[String]],
  body: String,
  isBase64Encoded: Boolean
)

object APIGatewayProxyResponse {
  implicit val encoder: JsonEncoder[APIGatewayProxyResponse] = DeriveJsonEncoder.gen[APIGatewayProxyResponse]
}
