package zio.lambda.internal

import zio._

trait RuntimeApi {
  def getNextInvocation(): Task[InvocationRequest]
  def sendInvocationResponse(invocationResponse: InvocationResponse): Task[Unit]
  def sendInvocationError(invocationError: InvocationError): Task[Unit]
  def sendInitializationError(errorResponse: InvocationErrorResponse): Task[Unit]
}

object RuntimeApi {
  def getNextInvocation(): RIO[Has[RuntimeApi], InvocationRequest] =
    ZIO.serviceWith(_.getNextInvocation())

  def sendInvocationResponse(invocationResponse: InvocationResponse): RIO[Has[RuntimeApi], Unit] =
    ZIO.serviceWith(_.sendInvocationResponse(invocationResponse))

  def sendInvocationError(invocationError: InvocationError): RIO[Has[RuntimeApi], Unit] =
    ZIO.serviceWith(_.sendInvocationError(invocationError))

  def sendInitializationError(errorResponse: InvocationErrorResponse): RIO[Has[RuntimeApi], Unit] =
    ZIO.serviceWith(_.sendInitializationError(errorResponse))
}
