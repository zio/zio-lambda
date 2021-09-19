package zio.lambda

import zio.json._

final case class InvocationError(
  errorMessage: String,
  errorType: String,
  stackTrace: List[String]
)

object InvocationError {
  implicit val encoder: JsonEncoder[InvocationError] = DeriveJsonEncoder.gen[InvocationError]

  def fromThrowable(throwable: Throwable): InvocationError =
    InvocationError(
      throwable.getMessage(),
      throwable.getClass().toGenericString(),
      throwable.getStackTrace().map(_.toString()).toList
    )
}
