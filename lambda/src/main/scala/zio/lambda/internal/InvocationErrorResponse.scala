package zio.lambda.internal

import zio.json._

final case class InvocationErrorResponse(
  errorMessage: String,
  errorType: String,
  stackTrace: List[String]
)

object InvocationErrorResponse {
  implicit val encoder: JsonEncoder[InvocationErrorResponse] = DeriveJsonEncoder.gen[InvocationErrorResponse]

  def fromThrowable(throwable: Throwable): InvocationErrorResponse =
    InvocationErrorResponse(
      throwable.getMessage(),
      throwable.getClass().getName(),
      throwable.getStackTrace().map(_.toString()).toList
    )
}
