package zio.lambda.response

import zio.json._

/**
 * The response that a Lambda function returns to an API Gateway HTTP API that uses payload format 2.0. Return it from
 * a handler that receives a `zio.lambda.event.APIGatewayV2HttpEvent`.
 *
 * Every field except `body` has a default value, so a response with only a body is a one-argument constructor call.
 * The encoder writes every field, including the defaults.
 *
 * {{{
 * import zio.json._
 *
 * val notFound = APIGatewayV2HTTPResponse(body = "No order 42", statusCode = 404)
 *
 * notFound.toJson
 * // {"body":"No order 42","statusCode":404,"headers":{},"multiValueHeaders":{},"cookies":[],"isBase64Encoded":false}
 * }}}
 *
 * @param body the response body
 * @param statusCode the HTTP status code, 200 by default
 * @param headers the response headers, one value per name
 * @param multiValueHeaders the response headers that have several values per name
 * @param cookies the cookies to set, each one a `Set-Cookie` header value
 * @param isBase64Encoded `true` when `body` holds Base64-encoded binary data
 *
 * @see [[https://zio.dev/zio-lambda/ ZIO Lambda]]
 */
final case class APIGatewayV2HTTPResponse(
  body: String,
  statusCode: Int = 200,
  headers: Map[String, String] = Map(),
  multiValueHeaders: Map[String, List[String]] = Map(),
  cookies: List[String] = List(),
  isBase64Encoded: Boolean = false
)

/** Holds the JSON encoder of [[APIGatewayV2HTTPResponse]]. */
object APIGatewayV2HTTPResponse {

  /**
   * Encodes the response as a JSON object with one key per field, named as the field.
   */
  implicit val encoder: JsonEncoder[APIGatewayV2HTTPResponse] = DeriveJsonEncoder.gen[APIGatewayV2HTTPResponse]
}
