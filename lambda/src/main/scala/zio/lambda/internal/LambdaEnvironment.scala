package zio.lambda.internal

import zio._
import zio.system._

final case class LambdaEnvironment(
  runtimeApi: String,
  handler: String,
  taskRoot: String,
  memoryLimitInMB: Int,
  logGroupName: String,
  logStreamName: String,
  functionName: String,
  functionVersion: String
)

object LambdaEnvironment {
  val live: ZLayer[System, Throwable, Has[LambdaEnvironment]] =
    (for {
      runtimeApi <-
        ZIO.require(new Throwable("AWS_LAMBDA_RUNTIME_API env variable not defined"))(
          env("AWS_LAMBDA_RUNTIME_API")
        )
      handler         <- envOrElse("_HANDLER", "")
      taskRoot        <- envOrElse("LAMBDA_TASK_ROOT", "")
      memoryLimitInMB <- envOrElse("AWS_LAMBDA_FUNCTION_MEMORY_SIZE", "128")
      logGroupName    <- envOrElse("AWS_LAMBDA_LOG_GROUP_NAME", "")
      logStreamName   <- envOrElse("AWS_LAMBDA_LOG_STREAM_NAME", "")
      functionName    <- envOrElse("AWS_LAMBDA_FUNCTION_NAME", "")
      functionVersion <- envOrElse("AWS_LAMBDA_FUNCTION_VERSION", "")
    } yield LambdaEnvironment(
      runtimeApi,
      handler,
      taskRoot,
      memoryLimitInMB.toInt,
      logGroupName,
      logStreamName,
      functionName,
      functionVersion
    )).toLayer

  def getEnvironment: RIO[Has[LambdaEnvironment], LambdaEnvironment] =
    ZIO.service[LambdaEnvironment]
}
