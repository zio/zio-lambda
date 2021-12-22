package zio.lambda.internal

import zio._

trait TestRuntimeApi {
  def addInvocationRequest(invocationRequest: InvocationRequest): Task[Unit]
  def getInvocationResponse(): Task[InvocationResponse]
  def getInvocationError(): Task[InvocationError]
  def getInitializationError(): Task[InvocationErrorResponse]
}

object TestRuntimeApi {

  final case class InvocationData(
    request: Option[InvocationRequest],
    response: Option[InvocationResponse],
    error: Option[InvocationError],
    initError: Option[InvocationErrorResponse]
  )

  final case class Test(invocationDataRef: Ref[InvocationData]) extends TestRuntimeApi with RuntimeApi {

    override def addInvocationRequest(invocationRequest: InvocationRequest): Task[Unit] =
      invocationDataRef.update(invocationData =>
        invocationData.copy(
          request = Option(invocationRequest)
        )
      )

    override def getInvocationResponse(): Task[InvocationResponse] =
      invocationDataRef.get
        .flatMap(invocationData =>
          ZIO
            .fromOption(invocationData.response)
            .mapError(Function.const(new Throwable("InvocationResponse not sent")))
        )

    override def getInvocationError(): Task[InvocationError] =
      invocationDataRef.get
        .flatMap(invocationData =>
          ZIO
            .fromOption(invocationData.error)
            .mapError(Function.const(new Throwable("InvocationError not sent")))
        )

    override def getInitializationError(): Task[InvocationErrorResponse] =
      invocationDataRef.get
        .flatMap(invocationData =>
          ZIO
            .fromOption(invocationData.initError)
            .mapError(Function.const(new Throwable("InitializationError not sent")))
        )

    override def getNextInvocation: Task[InvocationRequest] =
      invocationDataRef.get
        .flatMap(invocationData =>
          ZIO
            .fromOption(invocationData.request)
            .mapError(Function.const(new Throwable("InvocationRequest missing")))
        )

    override def sendInvocationResponse(invocationResponse: InvocationResponse): Task[Unit] =
      invocationDataRef.update(invocationData =>
        invocationData.copy(
          response = Option(invocationResponse)
        )
      )

    override def sendInvocationError(invocationError: InvocationError): Task[Unit] =
      invocationDataRef.update(invocationData =>
        invocationData.copy(
          error = Option(invocationError)
        )
      )

    override def sendInitializationError(errorResponse: InvocationErrorResponse): Task[Unit] = ???
  }

  val testLayer: ULayer[Has[TestRuntimeApi] with Has[RuntimeApi]] =
    Ref
      .make(InvocationData(None, None, None, None))
      .map { invocationData =>
        val test = Test(invocationData)
        Has.allOf[TestRuntimeApi, RuntimeApi](test, test)
      }
      .toLayerMany

  def addInvocationRequest(invocationRequest: InvocationRequest): RIO[Has[TestRuntimeApi], Unit] =
    ZIO.serviceWith(_.addInvocationRequest(invocationRequest))

  def getInvocationResponse(): RIO[Has[TestRuntimeApi], InvocationResponse] =
    ZIO.serviceWith(_.getInvocationResponse())

  def getInvocationError(): RIO[Has[TestRuntimeApi], InvocationError] =
    ZIO.serviceWith(_.getInvocationError())

}
