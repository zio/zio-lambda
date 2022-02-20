package zio.lambda.internal

import zio._
import zio.stream.ZStream

import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths

trait CustomClassLoader {
  def getClassLoader: Task[ClassLoader]
}

object CustomClassLoader {
  val live: URLayer[LambdaEnvironment, CustomClassLoader] = (
    (environment: LambdaEnvironment) =>
      new CustomClassLoader {
        override def getClassLoader: Task[ClassLoader] =
          ZStream
            .fromJavaStream(Files.list(Paths.get(environment.taskRoot)))
            .runCollect
            .map(stream =>
              new URLClassLoader(
                stream
                  .map(_.toUri().toURL())
                  .toArray
              )
            )
      }
  ).toLayer

  def getClassLoader: ZIO[CustomClassLoader, Throwable, ClassLoader] =
    ZIO.serviceWithZIO(_.getClassLoader)
}
