package zio.lambda.internal

import zio._
import zio.lambda.ZLambdaApp

trait ZRuntime {
  def processInvocation(eitherZLambda: Either[Throwable, ZLambdaApp[_, _]]): RIO[ZEnv, Unit]
}

object ZRuntime {
  def processInvocation(
    eitherZLambda: Either[Throwable, ZLambdaApp[_, _]]
  ): RIO[Has[ZRuntime] with ZEnv, Unit] =
    ZIO.accessM(_.get.processInvocation(eitherZLambda))
}
