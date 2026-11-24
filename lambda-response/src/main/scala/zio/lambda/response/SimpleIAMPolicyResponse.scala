package zio.lambda.response

import zio.json._

/**
 * A placeholder for the response of an API Gateway Lambda authorizer. It has no fields, so it encodes to an empty JSON
 * object, `{}`, and carries no policy. To answer an authorizer request, return a case class of your own that has a
 * `JsonEncoder`.
 */
final case class SimpleIAMPolicyResponse()

/** Holds the JSON encoder of [[SimpleIAMPolicyResponse]]. */
object SimpleIAMPolicyResponse {

  /** Encodes every value as an empty JSON object, `{}`. */
  implicit val encoder: JsonEncoder[SimpleIAMPolicyResponse] = DeriveJsonEncoder.gen[SimpleIAMPolicyResponse]
}
