package zio.lambda.response

import zio.json._

final case class ApplicationLoadBalancerResponse(
  statusCode: Int,
  statusDescription: String,
  isBase64Encoded: Boolean,
  headers: Map[String, String],
  multiValueHeaders: Map[String, List[String]],
  body: String
)

object ApplicationLoadBalancerResponse {
  implicit val encoder: JsonEncoder[ApplicationLoadBalancerResponse] =
    DeriveJsonEncoder.gen[ApplicationLoadBalancerResponse]

}
