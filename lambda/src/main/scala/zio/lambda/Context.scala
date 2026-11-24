package zio.lambda

import zio.lambda.internal.InvocationRequest
import zio.lambda.internal.LambdaEnvironment

/**
 * Describes one Lambda invocation. The runner builds a new `Context` for each invocation and passes it to the handler
 * as its second argument, next to the event.
 *
 * Read it to log the request ID, to name the function in metrics, or to check the deadline. The constructor is public,
 * so a test can build a `Context` with fixed values and call the handler directly.
 *
 * {{{
 * import zio._
 *
 * val handler = (event: String, context: Context) =>
 *   ZIO.logInfo(context.functionName + " handles " + context.awsRequestId).as(event.toUpperCase)
 *
 * val testContext = Context(
 *   awsRequestId = "c6af9ac6-7b61-11e6-9a41-93e8deadbeef",
 *   logGroupName = "/aws/lambda/shout",
 *   logStreamName = "2026/11/23/[7]0123456789abcdef",
 *   functionName = "shout",
 *   functionVersion = "7",
 *   invokedFunctionArn = "arn:aws:lambda:eu-west-1:123456789012:function:shout",
 *   remainingTimeInMillis = 1795459970063L,
 *   memoryLimitInMB = 512,
 *   clientContext = None,
 *   cognitoIdentity = None
 * )
 *
 * val result: UIO[String] = handler("hello", testContext)
 * }}}
 *
 * @param awsRequestId the ID of the invocation, from the `Lambda-Runtime-Aws-Request-Id` header
 * @param logGroupName the CloudWatch Logs group of the function, from the `AWS_LAMBDA_LOG_GROUP_NAME` environment
 *   variable, or `""` when it is not set
 * @param logStreamName the CloudWatch Logs stream of the function, from the `AWS_LAMBDA_LOG_STREAM_NAME` environment
 *   variable, or `""` when it is not set
 * @param functionName the name of the function, from the `AWS_LAMBDA_FUNCTION_NAME` environment variable, or `""`
 *   when it is not set
 * @param functionVersion the version of the function, from the `AWS_LAMBDA_FUNCTION_VERSION` environment variable, or
 *   `""` when it is not set
 * @param invokedFunctionArn the ARN used to invoke the function, from the `Lambda-Runtime-Invoked-Function-Arn` header
 * @param remainingTimeInMillis despite its name, not a duration: the value of the `Lambda-Runtime-Deadline-Ms` header,
 *   the Unix time in milliseconds at which the invocation times out. The time left is
 *   `remainingTimeInMillis - java.lang.System.currentTimeMillis()`.
 * @param memoryLimitInMB the memory configured for the function, from the `AWS_LAMBDA_FUNCTION_MEMORY_SIZE`
 *   environment variable, or 128 when it is not set
 * @param clientContext the client context of an invocation from the AWS Mobile SDK, or `None` when the invocation
 *   has none
 * @param cognitoIdentity the Amazon Cognito identity of an invocation from the AWS Mobile SDK, or `None` when the
 *   invocation has none
 *
 * @see [[https://zio.dev/zio-lambda/ ZIO Lambda]]
 */
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

/**
 * Companion of [[Context]]. Its `from` method is private to the `zio.lambda` package; the generated `apply` and
 * `unapply` are public.
 */
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
