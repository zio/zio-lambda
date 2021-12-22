package zio.lambda.internal

import zio._
import sttp.client3._

object TestSttpClient {

  def test(sttpBackend: SttpBackend[Identity, Any]): ULayer[Has[SttpClient]] = ZLayer.succeed(
    new SttpClient {
      override def getSttpBackend: Task[SttpBackend[Identity, Any]] =
        ZIO.succeed(sttpBackend)
    }
  )
}
