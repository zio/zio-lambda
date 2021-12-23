package zio.lambda.response

import zio.json._

final case class APIGatewayV2WebSocketResponse(
  isBase64Encoded: Boolean,
  statusCode: Int,
  headers: Map[String, String],
  multiValueHeaders: Map[String, List[String]],
  body: String
)

object APIGatewayV2WebSocketResponse {
  implicit val encoder: JsonEncoder[APIGatewayV2WebSocketResponse] =
    DeriveJsonEncoder.gen[APIGatewayV2WebSocketResponse]

}
