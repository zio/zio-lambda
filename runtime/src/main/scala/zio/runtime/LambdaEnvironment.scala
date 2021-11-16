package zio.runtime

import zio._
import zio.system._
import scala.util.Try

final case class LambdaEnvironment(
  lambdaClass: String,
  taskRoot: String,
  runtimeApi: String,
  memoryLimit: Int,
  logGroupName: String,
  logStreamName: String,
  functionName: String,
  functionVersion: String
)

object LambdaEnvironment {
  val live: ZLayer[System, Throwable, Has[LambdaEnvironment]] =
    (for {
      handler    <- envOrElse("_HANDLER", "")
      taskRoot   <- envOrElse("LAMBDA_TASK_ROOT", "")
      runtimeApi <- envOrElse("AWS_LAMBDA_RUNTIME_API", "")
      memoryLimit <-
        envOrElse("AWS_LAMBDA_FUNCTION_MEMORY_SIZE", "0").map {
          case ""         => 0
          case memorySize => Try(memorySize.toInt).toOption.getOrElse(0)
        }
      logGroupName    <- envOrElse("AWS_LAMBDA_LOG_GROUP_NAME", "")
      logStreamName   <- envOrElse("AWS_LAMBDA_LOG_STREAM_NAME", "")
      functionName    <- envOrElse("AWS_LAMBDA_FUNCTION_NAME", "")
      functionVersion <- envOrElse("AWS_LAMBDA_FUNCTION_VERSION", "")
    } yield LambdaEnvironment(
      handler,
      taskRoot,
      runtimeApi,
      memoryLimit,
      logGroupName,
      logStreamName,
      functionName,
      functionVersion
    )).toLayer
}
