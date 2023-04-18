package zio.lambda.internal

import zio._

import scala.annotation.nowarn

/**
 * The main class to use ZLambda as a Layer
 *
 * https://docs.aws.amazon.com/lambda/latest/dg/runtimes-custom.html
 * https://docs.aws.amazon.com/lambda/latest/dg/configuration-layers.html
 */
@nowarn("cat=deprecation")
@deprecated("Use ZLambdaAppReflectiveApp", "1.0.3")
object ZLambdaReflectiveApp extends ZIOAppDefault {

  def run =
    LambdaLoader.loadLambda
      .flatMap(LoopProcessor.loop(_).forever)
      .tapError(throwable =>
        RuntimeApi.sendInitializationError(
          InvocationErrorResponse.fromThrowable(throwable)
        )
      )
      .provide(
        LambdaEnvironment.live,
        CustomClassLoader.live,
        LambdaLoaderLive.layer,
        LoopProcessor.live,
        RuntimeApiLive.layer
      )

}

object ZLambdaAppReflectiveApp extends ZIOAppDefault {

  def run =
    LambdaLoader.loadLambdaApp
      .flatMap(v => LoopProcessor.loopZioApp(v).forever)
      .tapError(throwable =>
        RuntimeApi.sendInitializationError(
          InvocationErrorResponse.fromThrowable(throwable)
        )
      )
      .provide(
        LambdaEnvironment.live,
        CustomClassLoader.live,
        LambdaAppLoaderLive.layer,
        LoopProcessor.live,
        RuntimeApiLive.layer
      )

}
