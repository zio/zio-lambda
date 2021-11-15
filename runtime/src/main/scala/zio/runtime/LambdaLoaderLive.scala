package zio.runtime

import zio._
import zio.blocking._
import zio.console._
import zio.runtime.lambda.ZLambda

import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths
import scala.jdk.CollectionConverters._

final case class LambdaLoaderLive(environment: LambdaEnvironment, blocking: Blocking.Service, console: Console.Service)
    extends LambdaLoader {

  override def loadLambda(): Task[ZLambda[_, _]] =
    ZManaged
      .make(blocking.effectBlocking(Files.list(Paths.get(environment.taskRoot))))(stream => ZIO.succeed(stream.close()))
      .use { stream =>
        val classLoader = new URLClassLoader(
          stream
            .iterator()
            .asScala
            .map(_.toUri().toURL())
            .toArray,
          ClassLoader.getSystemClassLoader()
        )

        blocking
          .effectBlocking(
            Class
              .forName(
                environment.lambdaClass,
                true,
                classLoader
              )
              .getDeclaredConstructor()
              .newInstance()
              .asInstanceOf[ZLambda[_, _]]
          )
      }

}
