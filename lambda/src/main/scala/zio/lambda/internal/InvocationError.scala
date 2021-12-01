package zio.lambda.internal

final case class InvocationError(
  requestId: InvocationRequest.Id,
  errorResponse: InvocationErrorResponse
)
