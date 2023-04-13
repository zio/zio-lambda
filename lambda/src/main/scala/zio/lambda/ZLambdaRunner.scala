package zio.lambda

import zio.{RIO, ZIO}
import zio.lambda.internal.LoopProcessor

object ZLambdaRunner {

  def serve[R](app: ZLambdaApp[R,_,_]):RIO[R with LoopProcessor,Unit] =
    for {
      lp <- ZIO.service[LoopProcessor]
      res <- lp.loopZioApp(Right(app))
    } yield res

}
