package zio.lambda.internal

import zio._

object TestCustomClassLoader {
  lazy val test: ULayer[CustomClassLoader] =
    ZLayer.succeed(new CustomClassLoader {
      override lazy val getClassLoader: Task[ClassLoader] =
        ZIO.succeed(
          TestCustomClassLoader.getClass().getClassLoader()
        )
    })
}
