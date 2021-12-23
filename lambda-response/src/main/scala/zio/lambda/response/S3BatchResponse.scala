package zio.lambda.response

import zio.json._

final case class S3BatchResponse()

object S3BatchResponse {
  implicit val encoder: JsonEncoder[S3BatchResponse] = DeriveJsonEncoder.gen[S3BatchResponse]
}
