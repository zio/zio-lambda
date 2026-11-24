package zio.lambda.response

import zio.json._

/**
 * The response that a Lambda function returns to an Application Load Balancer that has the function as a target.
 * Return it from a handler that receives a `zio.lambda.event.ApplicationLoadBalancerRequestEvent`.
 *
 * No field has a default value, so every field must be passed.
 *
 * {{{
 * val page = ApplicationLoadBalancerResponse(
 *   statusCode = 200,
 *   statusDescription = "200 OK",
 *   isBase64Encoded = false,
 *   headers = Map("Content-Type" -> "text/plain"),
 *   multiValueHeaders = Map.empty,
 *   body = "healthy"
 * )
 * }}}
 *
 * @param statusCode the HTTP status code
 * @param statusDescription the status line text, such as `"200 OK"`
 * @param isBase64Encoded `true` when `body` holds Base64-encoded binary data
 * @param headers the response headers, one value per name
 * @param multiValueHeaders the response headers that have several values per name
 * @param body the response body
 *
 * @see [[https://zio.dev/zio-lambda/ ZIO Lambda]]
 */
final case class ApplicationLoadBalancerResponse(
  statusCode: Int,
  statusDescription: String,
  isBase64Encoded: Boolean,
  headers: Map[String, String],
  multiValueHeaders: Map[String, List[String]],
  body: String
)

/** Holds the JSON encoder of [[ApplicationLoadBalancerResponse]]. */
object ApplicationLoadBalancerResponse {

  /**
   * Encodes the response as a JSON object with one key per field, named as the field.
   */
  implicit val encoder: JsonEncoder[ApplicationLoadBalancerResponse] =
    DeriveJsonEncoder.gen[ApplicationLoadBalancerResponse]

}
