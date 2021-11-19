package zio.lambda

import zio._

trait ZRuntime {
  def processInvocation(eitherZLambda: Either[LambdaLoader.Error, ZLambda[_, _]]): RIO[ZEnv, Unit]
}

object ZRuntime {
  val layer: URLayer[Has[RuntimeApi], Has[ZRuntime]] =
    (ZRuntimeLive(_)).toLayer

  def processInvocation(eitherZLambda: Either[LambdaLoader.Error, ZLambda[_, _]]): RIO[Has[ZRuntime] with ZEnv, Unit] =
    ZIO.accessM(_.get.processInvocation(eitherZLambda))
}
