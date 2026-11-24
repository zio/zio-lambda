package zio.lambda.response

import zio.json._

/**
 * A placeholder for the response to an S3 Batch Operations job. It has no fields, so it encodes to an empty JSON
 * object, `{}`, and carries no task results. To answer S3 Batch Operations, return a case class of your own that has a
 * `JsonEncoder`.
 */
final case class S3BatchResponse()

/** Holds the JSON encoder of [[S3BatchResponse]]. */
object S3BatchResponse {

  /** Encodes every value as an empty JSON object, `{}`. */
  implicit val encoder: JsonEncoder[S3BatchResponse] = DeriveJsonEncoder.gen[S3BatchResponse]
}
