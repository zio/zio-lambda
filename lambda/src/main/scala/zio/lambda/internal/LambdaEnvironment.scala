package zio.lambda.internal

import zio._
import zio.System._

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
  val live: ZLayer[System, Throwable, LambdaEnvironment] =
    (for {
      runtimeApi <-
        env("AWS_LAMBDA_RUNTIME_API")
          .someOrFail(new Throwable("AWS_LAMBDA_RUNTIME_API env variable not defined"))

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

}
