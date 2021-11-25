package zio.lambda.internal

import zio._
import zio.lambda.ZLambda

trait ZRuntime {
  def processInvocation(eitherZLambda: Either[LambdaLoader.Error, ZLambda[_, _]]): RIO[ZEnv, Unit]
}

object ZRuntime {
  val layer: URLayer[Has[RuntimeApi], Has[ZRuntime]] =
    (ZRuntimeLive(_)).toLayer

  def processInvocation(eitherZLambda: Either[LambdaLoader.Error, ZLambda[_, _]]): RIO[Has[ZRuntime] with ZEnv, Unit] =
    ZIO.accessM(_.get.processInvocation(eitherZLambda))
}
