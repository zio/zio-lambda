package zio.lambda.response

import zio.json._

/**
 * The response that a Lambda function returns to an API Gateway REST API that uses the Lambda proxy integration. Return
 * it from a handler that receives a `zio.lambda.event.APIGatewayProxyRequestEvent`.
 *
 * No field has a default value, so every field must be passed.
 *
 * {{{
 * val created = APIGatewayProxyResponse(
 *   statusCode = 201,
 *   headers = Map("Content-Type" -> "text/plain"),
 *   multiValueHeaders = Map.empty,
 *   body = "created",
 *   isBase64Encoded = false
 * )
 * }}}
 *
 * @param statusCode the HTTP status code
 * @param headers the response headers, one value per name
 * @param multiValueHeaders the response headers that have several values per name
 * @param body the response body
 * @param isBase64Encoded `true` when `body` holds Base64-encoded binary data
 *
 * @see [[https://zio.dev/zio-lambda/ ZIO Lambda]]
 */
final case class APIGatewayProxyResponse(
  statusCode: Int,
  headers: Map[String, String],
  multiValueHeaders: Map[String, List[String]],
  body: String,
  isBase64Encoded: Boolean
)

/** Holds the JSON encoder of [[APIGatewayProxyResponse]]. */
object APIGatewayProxyResponse {

  /**
   * Encodes the response as a JSON object with one key per field, named as the field.
   */
  implicit val encoder: JsonEncoder[APIGatewayProxyResponse] = DeriveJsonEncoder.gen[APIGatewayProxyResponse]
}
