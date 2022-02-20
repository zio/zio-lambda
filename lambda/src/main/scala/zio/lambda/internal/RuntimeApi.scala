package zio.lambda.internal

import zio._

trait RuntimeApi {
  def getNextInvocation: Task[InvocationRequest]
  def sendInvocationResponse(invocationResponse: InvocationResponse): Task[Unit]
  def sendInvocationError(invocationError: InvocationError): Task[Unit]
  def sendInitializationError(errorResponse: InvocationErrorResponse): Task[Unit]
}

object RuntimeApi {
  def getNextInvocation: RIO[RuntimeApi, InvocationRequest] =
    ZIO.serviceWithZIO(_.getNextInvocation)

  def sendInvocationResponse(invocationResponse: InvocationResponse): RIO[RuntimeApi, Unit] =
    ZIO.serviceWithZIO(_.sendInvocationResponse(invocationResponse))

  def sendInvocationError(invocationError: InvocationError): RIO[RuntimeApi, Unit] =
    ZIO.serviceWithZIO(_.sendInvocationError(invocationError))

  def sendInitializationError(errorResponse: InvocationErrorResponse): RIO[RuntimeApi, Unit] =
    ZIO.serviceWithZIO(_.sendInitializationError(errorResponse))
}
