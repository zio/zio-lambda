package zio.lambda.internal

/** An error report for the invocation with ID `requestId`. */
final case class InvocationError(requestId: String, errorResponse: InvocationErrorResponse)
