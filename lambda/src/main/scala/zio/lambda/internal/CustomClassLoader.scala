package zio.lambda.internal

import zio._
import zio.blocking.Blocking

import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths
import scala.jdk.CollectionConverters._

object CustomClassLoader {

  val live: RLayer[Has[LambdaEnvironment] with Blocking, Has[ClassLoader]] =
    ZLayer.fromServiceManaged { environment: LambdaEnvironment =>
      for {
        taskRoot <- ZIO.require(new Throwable("Task Root not defined"))(ZIO.succeed(environment.taskRoot)).toManaged_
        stream <- ZManaged
                    .make(blocking.effectBlocking(Files.list(Paths.get(environment.taskRoot.getOrElse("")))))(stream =>
                      ZIO.succeed(stream.close())
                    )
      } yield new URLClassLoader(
        stream
          .iterator()
          .asScala
          .map(_.toUri().toURL())
          .toArray
      )
    }

}
