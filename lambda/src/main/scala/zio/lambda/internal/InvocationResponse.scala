package zio.lambda.internal

final case class InvocationResponse(requestId: InvocationRequest.Id, payload: String)
