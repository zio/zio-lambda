package zio.lambda.internal

import zio._

/** Runs a deprecated `ZLambda` handler loaded by name, like [[ZLambdaAppReflective]] does for a `ZIOAppDefault`. */
@deprecated("Use ZLambdaAppReflective", "1.0.3")
object ZLambdaReflectiveAppOld extends ZIOAppDefault {

  /**
   * Loads the handler and runs the invocation loop with it. When loading fails with a missing class or a class-loader
   * build error, it answers every invocation with the error. Other loading errors end the app without answering.
   */
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
