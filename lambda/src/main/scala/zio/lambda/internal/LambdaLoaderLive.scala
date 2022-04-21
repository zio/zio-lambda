package zio.lambda.internal

import zio._
import zio.lambda.ZLambda

final case class LambdaLoaderLive(
  customClassLoader: CustomClassLoader,
  environment: LambdaEnvironment
) extends LambdaLoader {

  override lazy val loadLambda: UIO[Either[Throwable, ZLambda[_, _]]] =
    customClassLoader.getClassLoader
      .flatMap[Any, Throwable, ZLambda[_, _]](classLoader =>
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
              .asInstanceOf[ZLambda[_, _]]
          )
          .refineOrDie { case ex: ClassNotFoundException => ex }
      )
      .either

}

object LambdaLoaderLive {
  val layer: ZLayer[CustomClassLoader with LambdaEnvironment, Throwable, LambdaLoader] =
    ZLayer.fromFunction(LambdaLoaderLive.apply _)
}
