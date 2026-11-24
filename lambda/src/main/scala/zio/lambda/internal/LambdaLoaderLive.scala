package zio.lambda.internal

import zio._
import zio.lambda.ZLambda

/** Loads a deprecated `ZLambda` handler. */
@deprecated("Use LambdaAppLoaderLive", "1.0.3")
final case class LambdaLoaderLive(
  customClassLoader: CustomClassLoader,
  environment: LambdaEnvironment
) extends LambdaLoaderLiveCommon[ZLambda[_, _]](customClassLoader, environment)

/** Loads a `ZIOAppDefault` handler. */
final case class LambdaAppLoaderLive(
  customClassLoader: CustomClassLoader,
  environment: LambdaEnvironment
) extends LambdaLoaderLiveCommon[ZIOAppDefault](customClassLoader, environment)

/**
 * Loads the Scala `object` named by `_HANDLER` with the class loader of [[CustomClassLoader]] and casts it to `T`
 * without a type check.
 */
abstract class LambdaLoaderLiveCommon[T](customClassLoader: CustomClassLoader, environment: LambdaEnvironment)
    extends LambdaLoader[T] {

  /**
   * Loads the handler. A `ClassNotFoundException` and a class-loader build error, such as a missing
   * `LAMBDA_TASK_ROOT`, are returned in a `Left`; any other exception from loading it is a defect.
   */
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

/** Layers for [[LambdaLoaderLive]]. */
@deprecated("Use LambdaAppLoaderLive", "1.0.3")
object LambdaLoaderLive {

  /** Builds a [[LambdaLoaderLive]] from a [[CustomClassLoader]] and a [[LambdaEnvironment]]. */
  val layer =
    ZLayer.fromFunction(LambdaLoaderLive.apply _)
}

/** Layers for [[LambdaAppLoaderLive]]. */
object LambdaAppLoaderLive {

  /** Builds a [[LambdaAppLoaderLive]] from a [[CustomClassLoader]] and a [[LambdaEnvironment]]. */
  val layer =
    ZLayer.fromFunction(LambdaAppLoaderLive.apply _)
}
