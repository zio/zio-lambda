package zio.lambda.internal

import zio._

/**
 * The main class to use ZLambda as a Layer
 *
 * https://docs.aws.amazon.com/lambda/latest/dg/runtimes-custom.html
 * https://docs.aws.amazon.com/lambda/latest/dg/configuration-layers.html
 */
object ZLambdaAppReflective extends ZIOAppDefault { self =>

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
