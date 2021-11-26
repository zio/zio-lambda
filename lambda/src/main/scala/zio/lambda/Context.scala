package zio.lambda

import zio.lambda.internal.InvocationRequest
import zio.lambda.internal.LambdaEnvironment

final case class Context(
  awsRequestId: String,
  logGroupName: Option[String],
  logStreamName: Option[String],
  functionName: Option[String],
  functionVersion: Option[String],
  invokedFunctionArn: Option[String],
  remainingTimeInMillis: Option[Int],
  memoryLimitInMB: Int,
  clientContext: Option[ClientContext],
  cognitoIdentity: Option[CognitoIdentity]
)

object Context {
  def from(invocationRequest: InvocationRequest, environment: LambdaEnvironment): Context =
    Context(
      awsRequestId = invocationRequest.id.value,
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
