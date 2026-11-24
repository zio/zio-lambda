package zio.lambda.internal

import zio._

/**
 * The main class of the packaged `zio-lambda` runtime, for a runtime that is deployed apart from the handler, for
 * example in a Lambda layer. It loads the Scala `object` named by the `_HANDLER` environment variable, with a class
 * loader that has one class-path entry for each file and folder directly inside `LAMBDA_TASK_ROOT`, and runs that
 * object's `run` as a `ZIOAppDefault`.
 *
 * When the class is not found, it reports an initialization error to the Lambda Runtime API and fails. When the object
 * is not a `ZIOAppDefault`, it dies with a `ClassCastException` and reports nothing.
 *
 * @see [[https://docs.aws.amazon.com/lambda/latest/dg/runtimes-custom.html Custom Lambda runtimes]]
 * @see [[https://docs.aws.amazon.com/lambda/latest/dg/chapter-layers.html Lambda layers]]
 */
object ZLambdaAppReflective extends ZIOAppDefault { self =>

  /** Loads the handler object and runs it. */
  def run =
    LambdaLoader.loadLambdaApp.flatMap { v =>
      ZIO.fromEither(v).flatMap(_.run)
    }.tapError {
      case throwable: Throwable =>
        RuntimeApi.sendInitializationError(
          InvocationErrorResponse.fromThrowable(throwable)
        )
      case any =>
        RuntimeApi.sendInitializationError(
          InvocationErrorResponse.fromThrowable(new IllegalStateException(any.toString))
        )
    }.provideSome[Scope with ZIOAppArgs](
      LambdaEnvironment.live,
      CustomClassLoader.live,
      LambdaAppLoaderLive.layer,
      RuntimeApiLive.layer
    )

}
