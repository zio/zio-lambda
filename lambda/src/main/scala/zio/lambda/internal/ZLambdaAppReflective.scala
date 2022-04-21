package zio.lambda.internal

import zio._

/**
 * The main class to use ZLambda as a Layer
 *
 * https://docs.aws.amazon.com/lambda/latest/dg/runtimes-custom.html
 * https://docs.aws.amazon.com/lambda/latest/dg/configuration-layers.html
 */
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
