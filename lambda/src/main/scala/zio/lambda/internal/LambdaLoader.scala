package zio.lambda.internal

import zio._
import zio.lambda.{ZLambda, ZLambdaApp}

trait LambdaLoader[T] {
  def loadLambda: UIO[Either[Throwable, T]]
}

object LambdaLoader {
  @deprecated("Use LambdaAppLoaderLive", "1.0.3")
  def loadLambda: URIO[LambdaLoader[ZLambda[_, _]], Either[Throwable, ZLambda[_, _]]] =
    ZIO.serviceWithZIO(_.loadLambda)

  def loadLambdaApp: URIO[LambdaLoader[ZLambdaApp[Any, _, _]], Either[Throwable, ZLambdaApp[Any, _, _]]] =
    ZIO.serviceWithZIO(_.loadLambda)
}
