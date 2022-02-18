package zio.lambda

import zio.lambda.internal.InvocationRequest
import zio.lambda.internal.LambdaEnvironment

final case class Context(
  awsRequestId: String,
  logGroupName: String,
  logStreamName: String,
  functionName: String,
  functionVersion: String,
  invokedFunctionArn: String,
  remainingTimeInMillis: Long,
  memoryLimitInMB: Int,
  clientContext: Option[ClientContext],
  cognitoIdentity: Option[CognitoIdentity]
)

object Context {
  private[lambda] def from(invocationRequest: InvocationRequest, environment: LambdaEnvironment): Context =
    Context(
      awsRequestId = invocationRequest.id,
      remainingTimeInMillis = invocationRequest.remainingTimeInMillis,
      clientContext = invocationRequest.clientContext,
      cognitoIdentity = invocationRequest.cognitoIdentity,
      invokedFunctionArn = invocationRequest.invokedFunctionArn,
      memoryLimitInMB = environment.memoryLimitInMB,
      logGroupName = environment.logGroupName,
      logStreamName = environment.logStreamName,
      functionName = environment.functionName,
      functionVersion = environment.functionVersion
    )
}
