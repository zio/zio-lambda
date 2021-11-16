package zio.runtime

import zio._
import zio.blocking._
import zio.console._
import zio.runtime.lambda.ZLambda

trait LambdaLoader {
  def loadLambda(): Task[ZLambda[_, _]]
}

object LambdaLoader {

  val layer: URLayer[Has[LambdaEnvironment] with Blocking with Console, Has[LambdaLoader]] =
    (LambdaLoaderLive(_, _, _)).toLayer

  def loadLambda(): RIO[Has[LambdaLoader], ZLambda[_, _]] =
    ZIO.serviceWith[LambdaLoader](_.loadLambda())

}
