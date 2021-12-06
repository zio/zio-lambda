package zio.lambda.internal

import zio._
import zio.blocking.Blocking
import zio.stream.ZStream

import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths

object CustomClassLoader {

  val layer: RLayer[Has[LambdaEnvironment] with Blocking, Has[ClassLoader]] =
    ZLayer.fromServiceM { environment: LambdaEnvironment =>
      for {
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

}
