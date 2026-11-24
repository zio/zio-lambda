package zio.lambda.internal

/** The response `payload` for the invocation with ID `requestId`, sent to Lambda as it is. */
final case class InvocationResponse(requestId: String, payload: String)
