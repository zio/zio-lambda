package zio.lambda.internal

import zio._

trait TestRuntimeApi {
  def addInvocationRequest(invocationRequest: InvocationRequest): Task[Unit]
  def getInvocationResponse(): Task[InvocationResponse]
  def getInvocationError(): Task[InvocationError]
  def getInitializationError(): Task[InvocationErrorResponse]
}

object TestRuntimeApi {

  final case class Test(
    invocationRequestQueue: Queue[InvocationRequest],
    invocationResponseQueue: Queue[InvocationResponse],
    invocationErrorQueue: Queue[InvocationError],
    initializationErrorQueue: Queue[InvocationErrorResponse]
  ) extends TestRuntimeApi
      with RuntimeApi {

    override def addInvocationRequest(
      invocationRequest: InvocationRequest
    ): Task[Unit] =
      invocationRequestQueue.offer(invocationRequest).unit

    override def getInvocationResponse(): Task[InvocationResponse] =
      invocationResponseQueue.take

    override def getInvocationError(): Task[InvocationError] =
      invocationErrorQueue.take

    override def getInitializationError(): Task[InvocationErrorResponse] =
      initializationErrorQueue.take

    override def getNextInvocation: Task[InvocationRequest] =
      invocationRequestQueue.take

    override def sendInvocationResponse(invocationResponse: InvocationResponse): Task[Unit] =
      invocationResponseQueue.offer(invocationResponse).unit

    override def sendInvocationError(invocationError: InvocationError): Task[Unit] =
      invocationErrorQueue.offer(invocationError).unit

    override def sendInitializationError(errorResponse: InvocationErrorResponse): Task[Unit] =
      initializationErrorQueue.offer(errorResponse).unit
  }

  val testLayer: ULayer[Has[TestRuntimeApi] with Has[RuntimeApi]] =
    (for {
      invocationRequestQueue   <- Queue.unbounded[InvocationRequest]
      invocationResponseQueue  <- Queue.unbounded[InvocationResponse]
      invocationErrorQueue     <- Queue.unbounded[InvocationError]
      initializationErrorQueue <- Queue.unbounded[InvocationErrorResponse]
    } yield {
      val test = Test(invocationRequestQueue, invocationResponseQueue, invocationErrorQueue, initializationErrorQueue)
      Has.allOf[TestRuntimeApi, RuntimeApi](test, test)
    }).toLayerMany

  def addInvocationRequest(invocationRequest: InvocationRequest): RIO[Has[TestRuntimeApi], Unit] =
    ZIO.serviceWith(_.addInvocationRequest(invocationRequest))

  def getInvocationResponse(): RIO[Has[TestRuntimeApi], InvocationResponse] =
    ZIO.serviceWith(_.getInvocationResponse())

  def getInvocationError(): RIO[Has[TestRuntimeApi], InvocationError] =
    ZIO.serviceWith(_.getInvocationError())

}
