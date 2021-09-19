package zio.lambda

import zio._
import zio.system._

import scala.util.Try

final case class LambdaEnvironment(
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
      runtimeApi <- env("AWS_LAMBDA_RUNTIME_API")
                      .someOrFail(new NoSuchFieldException("AWS_LAMBDA_RUNTIME_API missing"))
      memoryLimit <- envOrElse("AWS_LAMBDA_FUNCTION_MEMORY_SIZE", "0")
                       .flatMap(memorySize => ZIO.fromTry(Try(memorySize.toInt)))
      logGroupName    <- envOrElse("AWS_LAMBDA_LOG_GROUP_NAME", "")
      logStreamName   <- envOrElse("AWS_LAMBDA_LOG_STREAM_NAME", "")
      functionName    <- envOrElse("AWS_LAMBDA_FUNCTION_NAME", "")
      functionVersion <- envOrElse("AWS_LAMBDA_FUNCTION_VERSION", "")
    } yield LambdaEnvironment(
      runtimeApi,
      memoryLimit.toInt,
      logGroupName,
      logStreamName,
      functionName,
      functionVersion
    )).toLayer
}
