package zio.lambda.internal

import zio._
import zio.blocking._
import zio.lambda.ZLambda

trait LambdaLoader {
  def loadLambda(): UIO[Either[LambdaLoader.Error, ZLambda[_, _]]]
}

object LambdaLoader {
  final case class Error(errorMessage: String, errorType: Error.Type)
  object Error {
    def userError(errorMessage: String): Error =
      Error(errorMessage, Type.UserError)

    def zLambdaNotFound(errorMessage: String): Error =
      Error(errorMessage, Type.ZLambdaNotFound)

    sealed trait Type
    object Type {
      case object UserError       extends Type
      case object ZLambdaNotFound extends Type
    }
  }

  val layer: URLayer[Has[LambdaEnvironment] with Blocking, Has[LambdaLoader]] =
    (LambdaLoaderLive(_, _)).toLayer

  def loadLambda(): URIO[Has[LambdaLoader], Either[LambdaLoader.Error, ZLambda[_, _]]] =
    ZIO.serviceWith(_.loadLambda())

}
