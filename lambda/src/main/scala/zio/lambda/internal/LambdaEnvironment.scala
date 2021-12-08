package zio.lambda.internal

import zio._

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

object LambdaEnvironment {
  def getEnvironment: RIO[Has[LambdaEnvironment], LambdaEnvironment] =
    ZIO.service[LambdaEnvironment]
}
