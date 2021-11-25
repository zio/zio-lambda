package zio.lambda.internal

import zio.json._
import zio.lambda.internal.LambdaLoader.Error.Type._

final case class InvocationErrorResponse(
  errorMessage: String,
  errorType: String,
  stackTrace: List[String]
)

object InvocationErrorResponse {
  implicit val encoder: JsonEncoder[InvocationErrorResponse] = DeriveJsonEncoder.gen[InvocationErrorResponse]

  def fromLambdaLoaderError(error: LambdaLoader.Error): InvocationErrorResponse =
    InvocationErrorResponse(
      error.errorMessage,
      error.errorType match {
        case UserError       => "UserError"
        case ZLambdaNotFound => "ZLambdaNotFound"
      },
      Nil
    )

  def fromThrowable(throwable: Throwable): InvocationErrorResponse =
    InvocationErrorResponse(
      throwable.getMessage(),
      throwable.getClass().toGenericString(),
      throwable.getStackTrace().map(_.toString()).toList
    )
}
