package zio.lambda.internal

import zio._
import zio.lambda.ZLambda

@deprecated("Use LambdaAppLoaderLive", "1.0.3")
final case class LambdaLoaderLive(
  customClassLoader: CustomClassLoader,
  environment: LambdaEnvironment
) extends LambdaLoaderLiveCommon[ZLambda[_, _]](customClassLoader, environment)

final case class LambdaAppLoaderLive(
  customClassLoader: CustomClassLoader,
  environment: LambdaEnvironment
) extends LambdaLoaderLiveCommon[ZIOAppDefault](customClassLoader, environment)

abstract class LambdaLoaderLiveCommon[T](customClassLoader: CustomClassLoader, environment: LambdaEnvironment)
    extends LambdaLoader[T] {

  override lazy val loadLambda: UIO[Either[Throwable, T]] =
    customClassLoader.getClassLoader
      .flatMap[Any, Throwable, T](classLoader =>
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
              .asInstanceOf[T]
          )
          .refineOrDie { case ex: ClassNotFoundException => ex }
      )
      .either

}

@deprecated("Use LambdaAppLoaderLive", "1.0.3")
object LambdaLoaderLive {
  val layer =
    ZLayer.fromFunction(LambdaLoaderLive.apply _)
}

object LambdaAppLoaderLive {
  val layer =
    ZLayer.fromFunction(LambdaAppLoaderLive.apply _)
}
