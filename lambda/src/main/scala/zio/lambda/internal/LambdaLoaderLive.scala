package zio.lambda.internal

import zio._
import zio.lambda.ZLambdaApp

final case class LambdaLoaderLive(
  customClassLoader: CustomClassLoader,
  environment: LambdaEnvironment
) extends LambdaLoader {

  override lazy val loadLambda: UIO[Either[Throwable, ZLambdaApp[_, _]]] =
    customClassLoader.getClassLoader
      .flatMap[Any, Throwable, ZLambdaApp[_, _]](classLoader =>
        ZIO
          .attempt(
            Class
              .forName(
                environment.handler + "$",
                true,
                classLoader
              )
              .getDeclaredField("MODULE$")
              .get(null)
              .asInstanceOf[ZLambdaApp[_, _]]
          )
          .refineOrDie { case ex: ClassNotFoundException => ex }
      )
      .either

}

object LambdaLoaderLive {
  val layer: ZLayer[CustomClassLoader with LambdaEnvironment, Throwable, LambdaLoader] =
    (LambdaLoaderLive(_, _)).toLayer
}
