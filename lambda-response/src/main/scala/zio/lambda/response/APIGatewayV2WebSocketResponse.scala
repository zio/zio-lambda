package zio.lambda.response

import zio.json._

/**
 * The response that a Lambda function returns to an API Gateway WebSocket API. Return it from a handler that receives
 * a `zio.lambda.event.APIGatewayV2WebSocketEvent`.
 *
 * No field has a default value, and `isBase64Encoded` is the first parameter.
 *
 * {{{
 * import zio.json._
 *
 * val pong = APIGatewayV2WebSocketResponse(
 *   isBase64Encoded = false,
 *   statusCode = 200,
 *   headers = Map.empty,
 *   multiValueHeaders = Map.empty,
 *   body = "pong"
 * )
 *
 * pong.toJson
 * // {"isBase64Encoded":false,"statusCode":200,"headers":{},"multiValueHeaders":{},"body":"pong"}
 * }}}
 *
 * @param isBase64Encoded `true` when `body` holds Base64-encoded binary data
 * @param statusCode the status code
 * @param headers the response headers, one value per name
 * @param multiValueHeaders the response headers that have several values per name
 * @param body the response body
 *
 * @see [[https://zio.dev/zio-lambda/ ZIO Lambda]]
 */
final case class APIGatewayV2WebSocketResponse(
  isBase64Encoded: Boolean,
  statusCode: Int,
  headers: Map[String, String],
  multiValueHeaders: Map[String, List[String]],
  body: String
)

/** Holds the JSON encoder of [[APIGatewayV2WebSocketResponse]]. */
object APIGatewayV2WebSocketResponse {

  /**
   * Encodes the response as a JSON object with one key per field, named as the field.
   */
  implicit val encoder: JsonEncoder[APIGatewayV2WebSocketResponse] =
    DeriveJsonEncoder.gen[APIGatewayV2WebSocketResponse]

}
