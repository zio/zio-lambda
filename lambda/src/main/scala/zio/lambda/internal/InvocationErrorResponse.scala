package zio.lambda.internal

import zio.json._

/** The JSON body of an invocation or initialization error sent to the Lambda Runtime API. */
final case class InvocationErrorResponse(
  errorMessage: String,
  errorType: String,
  stackTrace: List[String]
)

/** Constructors and codecs for [[InvocationErrorResponse]]. */
object InvocationErrorResponse {

  /** Encodes the error as a JSON object with the keys `errorMessage`, `errorType` and `stackTrace`. */
  implicit val encoder: JsonEncoder[InvocationErrorResponse] = DeriveJsonEncoder.gen[InvocationErrorResponse]

  /** Builds an error from the message, class name and stack trace of `throwable`. */
  def fromThrowable(throwable: Throwable): InvocationErrorResponse =
    InvocationErrorResponse(
      throwable.getMessage(),
      throwable.getClass().getName(),
      throwable.getStackTrace().map(_.toString()).toList
    )
}
