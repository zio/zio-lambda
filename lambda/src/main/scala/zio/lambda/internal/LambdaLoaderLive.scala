package zio.lambda.internal

import zio._
import zio.blocking._
import zio.lambda.ZLambdaApp

final case class LambdaLoaderLive(classLoader: ClassLoader, environment: LambdaEnvironment, blocking: Blocking.Service)
    extends LambdaLoader {

  override def loadLambda(): UIO[Either[Throwable, ZLambdaApp[_, _]]] =
    (for {
      handler <-
        ZIO.require(new Throwable("Function Handler not defined"))(ZIO.succeed(environment.handler))
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
                       .asInstanceOf[ZLambdaApp[_, _]]
                   )
                   .refineOrDie { case ex: ClassNotFoundException => ex }
    } yield zLambda).either

}

object LambdaLoaderLive {
  val layer: URLayer[Has[ClassLoader] with Has[LambdaEnvironment] with Blocking, Has[LambdaLoader]] =
    (LambdaLoaderLive(_, _, _)).toLayer
}
