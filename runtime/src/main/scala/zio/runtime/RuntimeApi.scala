package zio.runtime

import sttp.client3._
import zio._
import zio.blocking.Blocking

trait RuntimeApi {
  def nextInvocation(): Task[InvocationRequest]
  def invocationResponse(requestId: InvocationRequest.Id, response: String): Task[Unit]
  def invocationError(requestId: InvocationRequest.Id, error: InvocationError): Task[Unit]
  def initializationError(error: InvocationError): Task[Unit]
}

object RuntimeApi {
  val layer: URLayer[Blocking with Has[SttpBackend[Identity, Any]] with Has[LambdaEnvironment], Has[RuntimeApi]] =
    (RuntimeApiLive(_, _, _)).toLayer

  def initializationError(error: InvocationError): RIO[Has[RuntimeApi], Unit] =
    ZIO.serviceWith[RuntimeApi](_.initializationError(error))

}
