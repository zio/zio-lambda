package zio.lambda

import zio._

/**
 * https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html#runtimes-api-next
 */
trait RuntimeApi {
  def nextInvocation(): Task[InvocationRequest]
  def invocationResponse(requestId: InvocationRequest.Id, response: String): Task[Unit]
  def invocationError(requestId: InvocationRequest.Id, error: InvocationError): Task[Unit]
  def initializationError(error: InvocationError): Task[Unit]
}

object RuntimeApi {
  def nextInvocation(): RIO[Has[RuntimeApi], InvocationRequest] =
    ZIO.serviceWith[RuntimeApi](_.nextInvocation())

  def invocationResponse(requestId: InvocationRequest.Id, response: String): RIO[Has[RuntimeApi], Unit] =
    ZIO.serviceWith[RuntimeApi](_.invocationResponse(requestId, response))

  def invocationError(requestId: InvocationRequest.Id, error: InvocationError): RIO[Has[RuntimeApi], Unit] =
    ZIO.serviceWith[RuntimeApi](_.invocationError(requestId, error))

  def initializationError(error: InvocationError): RIO[Has[RuntimeApi], Unit] =
    ZIO.serviceWith[RuntimeApi](_.initializationError(error))
}
