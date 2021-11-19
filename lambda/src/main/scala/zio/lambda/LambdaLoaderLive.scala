package zio.lambda

import zio._
import zio.blocking._
import zio.console._
import zio.lambda.LambdaLoader.Error

import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths
import scala.jdk.CollectionConverters._

final case class LambdaLoaderLive(environment: LambdaEnvironment, blocking: Blocking.Service, console: Console.Service)
    extends LambdaLoader {

  override def loadLambda(): UIO[Either[Error, ZLambda[_, _]]] =
    (for {
      taskRoot <-
        ZIO.require(Error.userError("Task Root not defined"))(ZIO.succeed(environment.taskRoot))
      handler <-
        ZIO.require(Error.userError("Function Handler not defined"))(ZIO.succeed(environment.handler))
      zLambda <-
        ZManaged
          .make(blocking.effectBlocking(Files.list(Paths.get(taskRoot))))(stream => ZIO.succeed(stream.close()))
          .use[Any, Throwable, ZLambda[_, _]] { stream =>
            for {
              systemClassLoader <- blocking.effectBlocking(ClassLoader.getSystemClassLoader())
              classLoader = new URLClassLoader(
                              stream
                                .iterator()
                                .asScala
                                .map(_.toUri().toURL())
                                .toArray
                            )
              zLambda <- blocking
                           .effectBlocking(
                             Class
                               .forName(
                                 handler + "$",
                                 true,
                                 classLoader
                               )
                               .getDeclaredField("MODULE$")
                               .get(null)
                               .asInstanceOf[ZLambda[_, _]]
                           )
            } yield zLambda
          }
          .refineOrDie { case ex: ClassNotFoundException =>
            LambdaLoader.Error.zLambdaNotFound(ex.getMessage())
          }
    } yield zLambda).either

}
