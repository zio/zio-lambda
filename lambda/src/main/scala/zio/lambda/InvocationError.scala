package zio.lambda

final case class InvocationError(
  requestId: InvocationRequest.Id,
  errorResponse: InvocationErrorResponse
)
