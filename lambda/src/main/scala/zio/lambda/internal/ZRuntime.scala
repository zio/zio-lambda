package zio.lambda.internal

import zio._
import zio.lambda.ZLambda

trait ZRuntime {
  def processInvocation(eitherZLambda: Either[Throwable, ZLambda[_, _]]): RIO[ZEnv, Unit]
}

object ZRuntime {
  def processInvocation(eitherZLambda: Either[Throwable, ZLambda[_, _]]): RIO[Has[ZRuntime] with ZEnv, Unit] =
    ZIO.accessM(_.get.processInvocation(eitherZLambda))
}
