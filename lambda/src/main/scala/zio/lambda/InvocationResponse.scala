package zio.lambda

final case class InvocationResponse(requestId: InvocationRequest.Id, payload: String)
