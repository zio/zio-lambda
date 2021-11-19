package zio.lambda

import zio._
import zio.system._

import scala.util.Try

final case class LambdaEnvironment(
  runtimeApi: String,
  handler: Option[String],
  taskRoot: Option[String],
  memoryLimit: Option[Int],
  logGroupName: Option[String],
  logStreamName: Option[String],
  functionName: Option[String],
  functionVersion: Option[String]
)

object LambdaEnvironment {
  val live: ZLayer[System, Throwable, Has[LambdaEnvironment]] =
    (for {
      runtimeApi <-
        ZIO.require(new Throwable("AWS_LAMBDA_RUNTIME_API env variable not defined"))(
          env("AWS_LAMBDA_RUNTIME_API")
        )
      handler         <- env("_HANDLER")
      taskRoot        <- env("LAMBDA_TASK_ROOT")
      memoryLimit     <- env("AWS_LAMBDA_FUNCTION_MEMORY_SIZE")
      logGroupName    <- env("AWS_LAMBDA_LOG_GROUP_NAME")
      logStreamName   <- env("AWS_LAMBDA_LOG_STREAM_NAME")
      functionName    <- env("AWS_LAMBDA_FUNCTION_NAME")
      functionVersion <- env("AWS_LAMBDA_FUNCTION_VERSION")
    } yield LambdaEnvironment(
      runtimeApi,
      handler,
      taskRoot,
      memoryLimit.flatMap(mem => Try(mem.toInt).toOption),
      logGroupName,
      logStreamName,
      functionName,
      functionVersion
    )).toLayer
}
