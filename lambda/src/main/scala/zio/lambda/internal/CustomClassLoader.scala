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
  val live: URLayer[Has[LambdaEnvironment], Has[CustomClassLoader]] = (
    (environment: LambdaEnvironment) =>
      new CustomClassLoader {

        override lazy val getClassLoader: Task[ClassLoader] = for {
          taskRoot <- ZIO.require(new Throwable("Task Root not defined"))(ZIO.succeed(environment.taskRoot))
          stream <- ZStream
                      .fromJavaStream(Files.list(Paths.get(taskRoot)))
                      .runCollect
        } yield new URLClassLoader(
          stream
            .map(_.toUri().toURL())
            .toArray
        )

      }
  ).toLayer

  def getClassLoader: ZIO[Has[CustomClassLoader], Throwable, ClassLoader] =
    ZIO.serviceWith(_.getClassLoader)
}
