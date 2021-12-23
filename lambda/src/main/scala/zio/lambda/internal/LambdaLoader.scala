package zio.lambda.internal

import zio._
import zio.lambda.ZLambdaApp

trait LambdaLoader {
  def loadLambda: UIO[Either[Throwable, ZLambdaApp[_, _]]]
}

object LambdaLoader {
  def loadLambda: URIO[Has[LambdaLoader], Either[Throwable, ZLambdaApp[_, _]]] =
    ZIO.serviceWith(_.loadLambda)
}
