package zio.lambda.internal

import zio._
import zio.system._
import scala.util.Try

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
  val live: ZLayer[System, Throwable, Has[LambdaEnvironment]] =
    (for {
      runtimeApi <-
        ZIO.require(new Throwable("AWS_LAMBDA_RUNTIME_API env variable not defined"))(
          env("AWS_LAMBDA_RUNTIME_API")
        )
      handler         <- env("_HANDLER")
      taskRoot        <- env("LAMBDA_TASK_ROOT")
      memoryLimitInMB <- env("AWS_LAMBDA_FUNCTION_MEMORY_SIZE")
      logGroupName    <- env("AWS_LAMBDA_LOG_GROUP_NAME")
      logStreamName   <- env("AWS_LAMBDA_LOG_STREAM_NAME")
      functionName    <- env("AWS_LAMBDA_FUNCTION_NAME")
      functionVersion <- env("AWS_LAMBDA_FUNCTION_VERSION")
    } yield LambdaEnvironment(
      runtimeApi,
      handler,
      taskRoot,
      memoryLimitInMB.fold(128)(value => Try(value.toInt).getOrElse(128)),
      logGroupName,
      logStreamName,
      functionName,
      functionVersion
    )).toLayer

  def getEnvironment: RIO[Has[LambdaEnvironment], LambdaEnvironment] =
    ZIO.service[LambdaEnvironment]
}
