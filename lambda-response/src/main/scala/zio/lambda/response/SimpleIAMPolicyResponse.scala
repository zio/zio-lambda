package zio.lambda.response

import zio.json._

final case class SimpleIAMPolicyResponse()

object SimpleIAMPolicyResponse {
  implicit val encoder: JsonEncoder[SimpleIAMPolicyResponse] = DeriveJsonEncoder.gen[SimpleIAMPolicyResponse]
}
