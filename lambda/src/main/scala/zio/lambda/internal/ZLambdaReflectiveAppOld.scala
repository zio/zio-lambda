package zio.lambda.internal

import zio._

@deprecated("Use ZLambdaAppReflectiveApp", "1.0.3")
object ZLambdaReflectiveAppOld extends ZIOAppDefault {

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
