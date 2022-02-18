package zio.lambda.internal

final case class InvocationError(requestId: String, errorResponse: InvocationErrorResponse)
