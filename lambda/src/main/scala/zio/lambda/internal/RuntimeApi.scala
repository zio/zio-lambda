package zio.lambda.internal

import sttp.client3._
import zio._
import zio.blocking.Blocking

trait RuntimeApi {
  def getNextInvocation(): Task[InvocationRequest]
  def sendInvocationResponse(invocationResponse: InvocationResponse): Task[Unit]
  def sendInvocationError(invocationError: InvocationError): Task[Unit]
  def sendInitializationError(errorResponse: InvocationErrorResponse): Task[Unit]
}

object RuntimeApi {
  val layer: URLayer[Blocking with Has[SttpBackend[Identity, Any]] with Has[LambdaEnvironment], Has[RuntimeApi]] =
    (RuntimeApiLive(_, _, _)).toLayer

  def getNextInvocation(): RIO[Has[RuntimeApi], InvocationRequest] =
    ZIO.serviceWith(_.getNextInvocation())

  def sendInvocationResponse(invocationResponse: InvocationResponse): RIO[Has[RuntimeApi], Unit] =
    ZIO.serviceWith(_.sendInvocationResponse(invocationResponse))

  def sendInvocationError(invocationError: InvocationError): RIO[Has[RuntimeApi], Unit] =
    ZIO.serviceWith(_.sendInvocationError(invocationError))

  def sendInitializationError(errorResponse: InvocationErrorResponse): RIO[Has[RuntimeApi], Unit] =
    ZIO.serviceWith(_.sendInitializationError(errorResponse))
}
