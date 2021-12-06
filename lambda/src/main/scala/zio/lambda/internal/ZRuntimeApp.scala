package zio.lambda.internal

import zio._
import zio.blocking.Blocking

/**
 * The main class to use ZLambda as a Layer
 *
 * https://docs.aws.amazon.com/lambda/latest/dg/runtimes-custom.html
 * https://docs.aws.amazon.com/lambda/latest/dg/configuration-layers.html
 */
object ZRuntimeApp extends App {

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = {
    val classLoaderBuilderLayer = (LambdaEnvironment.live ++
      Blocking.live) >>> CustomClassLoader.live

    val lambdaLoaderLayer = (
      LambdaEnvironment.live ++
        Blocking.live ++
        classLoaderBuilderLayer
    ) >>> LambdaLoaderLive.layer

    val runtimeApiLayer = (
      LambdaEnvironment.live ++
        Blocking.live ++
        SttpClient.layer
    ) >>> RuntimeApiLive.layer

    val zRuntimeLayer = (runtimeApiLayer ++ LambdaEnvironment.live) >>> ZRuntime.layer

    LambdaLoader
      .loadLambda()
      .flatMap(ZRuntime.processInvocation(_).forever)
      .tapError(throwable => RuntimeApi.sendInitializationError(InvocationErrorResponse.fromThrowable(throwable)))
      .provideCustomLayer(zRuntimeLayer ++ lambdaLoaderLayer ++ runtimeApiLayer)
      .exitCode
  }

}
