package zio.lambda.internal

import zio._
import zio.lambda.ZLambda

/** A service that loads the handler object named by the `_HANDLER` environment variable. */
trait LambdaLoader[T] {

  /** Loads the handler, or returns the error that prevented it in a `Left`. */
  def loadLambda: UIO[Either[Throwable, T]]
}

/** Accessors for [[LambdaLoader]]. */
object LambdaLoader {

  /** Loads a deprecated `ZLambda` handler with the service of the environment. */
  @deprecated("Use LambdaAppLoaderLive", "1.0.3")
  def loadLambda: URIO[LambdaLoader[ZLambda[_, _]], Either[Throwable, ZLambda[_, _]]] =
    ZIO.serviceWithZIO(_.loadLambda)

  /** Loads a `ZIOAppDefault` handler with the service of the environment. */
  def loadLambdaApp: URIO[LambdaLoader[ZIOAppDefault], Either[Throwable, ZIOAppDefault]] =
    ZIO.serviceWithZIO(_.loadLambda)
}
