package zio.runtime

import zio._

trait ZRuntime {
  def processInvocation(f: String => RIO[ZEnv, String]): RIO[ZEnv, Unit]
}

object ZRuntime {
  val layer: URLayer[Has[RuntimeApi], Has[ZRuntime]] =
    (ZRuntimeLive(_)).toLayer

  def processInvocation(f: String => RIO[ZEnv, String]): RIO[Has[ZRuntime] with ZEnv, Unit] =
    ZIO.accessM(_.get.processInvocation(f))
}
