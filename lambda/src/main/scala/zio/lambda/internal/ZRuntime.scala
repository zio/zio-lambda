package zio.lambda.internal

import zio._
import zio.lambda.ZLambda

trait ZRuntime {
  def processInvocation(eitherZLambda: Either[LambdaLoader.Error, ZLambda[_, _]]): RIO[ZEnv, Unit]
}

object ZRuntime {
  val layer: URLayer[Has[RuntimeApi] with Has[LambdaEnvironment], Has[ZRuntime]] =
    (ZRuntimeLive(_, _)).toLayer

  def processInvocation(eitherZLambda: Either[LambdaLoader.Error, ZLambda[_, _]]): RIO[Has[ZRuntime] with ZEnv, Unit] =
    ZIO.accessM(_.get.processInvocation(eitherZLambda))
}
