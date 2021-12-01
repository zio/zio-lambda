package zio.lambda.internal

import zio._
import zio.blocking.Blocking
import zio.console._

/**
 * The main class to use ZLambda as a Layer
 *
 * https://docs.aws.amazon.com/lambda/latest/dg/runtimes-custom.html
 * https://docs.aws.amazon.com/lambda/latest/dg/configuration-layers.html
 */
object ZRuntimeApp extends App {

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = {
    val lambdaLoaderLayer = (
      LambdaEnvironment.live ++
        Blocking.live ++
        Console.live
    ) >>> LambdaLoader.layer

    val runtimeApiLayer = (
      LambdaEnvironment.live ++
        Blocking.live ++
        SttpClient.layer
    ) >>> RuntimeApi.layer

    val zRuntimeLayer = (runtimeApiLayer ++ LambdaEnvironment.live) >>> ZRuntime.layer

    LambdaLoader
      .loadLambda()
      .flatMap(ZRuntime.processInvocation(_).forever)
      .tapError(throwable =>
        RuntimeApi.sendInitializationError(
          InvocationErrorResponse.fromThrowable(throwable)
        )
      )
      .provideCustomLayer(zRuntimeLayer ++ lambdaLoaderLayer ++ runtimeApiLayer)
      .exitCode
  }

}
