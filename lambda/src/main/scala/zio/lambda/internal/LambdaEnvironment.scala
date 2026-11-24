package zio.lambda.internal

import zio._

/** The settings that Lambda passes to the runtime in environment variables. */
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

/** Constructors for [[LambdaEnvironment]]. */
object LambdaEnvironment {

  /**
   * Reads the environment variables. Fails when `AWS_LAMBDA_RUNTIME_API` is not set; the other values default to `""`,
   * and the memory size to 128.
   */
  val live: TaskLayer[LambdaEnvironment] =
    ZLayer {
      for {
        runtimeApi <- ZIO.system
                        .flatMap(_.env("AWS_LAMBDA_RUNTIME_API"))
                        .someOrFail(new Throwable("AWS_LAMBDA_RUNTIME_API env variable not defined"))
        handler         <- ZIO.system.flatMap(_.envOrElse("_HANDLER", ""))
        taskRoot        <- ZIO.system.flatMap(_.envOrElse("LAMBDA_TASK_ROOT", ""))
        memoryLimitInMB <- ZIO.system.flatMap(_.envOrElse("AWS_LAMBDA_FUNCTION_MEMORY_SIZE", "128"))
        logGroupName    <- ZIO.system.flatMap(_.envOrElse("AWS_LAMBDA_LOG_GROUP_NAME", ""))
        logStreamName   <- ZIO.system.flatMap(_.envOrElse("AWS_LAMBDA_LOG_STREAM_NAME", ""))
        functionName    <- ZIO.system.flatMap(_.envOrElse("AWS_LAMBDA_FUNCTION_NAME", ""))
        functionVersion <- ZIO.system.flatMap(_.envOrElse("AWS_LAMBDA_FUNCTION_VERSION", ""))

      } yield LambdaEnvironment(
        runtimeApi,
        handler,
        taskRoot,
        memoryLimitInMB.toInt,
        logGroupName,
        logStreamName,
        functionName,
        functionVersion
      )
    }

}
