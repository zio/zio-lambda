package zio.lambda.internal

final case class LambdaEnvironment(
  runtimeApi: String,
  handler: Option[String],
  taskRoot: Option[String],
  memoryLimitInMB: Int,
  logGroupName: Option[String],
  logStreamName: Option[String],
  functionName: Option[String],
  functionVersion: Option[String]
)
