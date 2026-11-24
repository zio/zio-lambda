package zio.lambda.internal

import zio._

/** A client for the Lambda Runtime API. */
trait RuntimeApi {

  /** Waits for the next invocation and returns it. */
  def getNextInvocation: Task[InvocationRequest]

  /** Sends the response of an invocation. */
  def sendInvocationResponse(invocationResponse: InvocationResponse): Task[Unit]

  /** Reports that an invocation failed. */
  def sendInvocationError(invocationError: InvocationError): Task[Unit]

  /** Reports that the runtime failed to initialize. */
  def sendInitializationError(errorResponse: InvocationErrorResponse): Task[Unit]
}

/** Accessors for the [[RuntimeApi]] service. */
object RuntimeApi {

  /** Waits for the next invocation with the [[RuntimeApi]] service. */
  def getNextInvocation: RIO[RuntimeApi, InvocationRequest] =
    ZIO.serviceWithZIO(_.getNextInvocation)

  /** Sends the response of an invocation with the [[RuntimeApi]] service. */
  def sendInvocationResponse(invocationResponse: InvocationResponse): RIO[RuntimeApi, Unit] =
    ZIO.serviceWithZIO(_.sendInvocationResponse(invocationResponse))

  /** Reports a failed invocation with the [[RuntimeApi]] service. */
  def sendInvocationError(invocationError: InvocationError): RIO[RuntimeApi, Unit] =
    ZIO.serviceWithZIO(_.sendInvocationError(invocationError))

  /** Reports an initialization error with the [[RuntimeApi]] service. */
  def sendInitializationError(errorResponse: InvocationErrorResponse): RIO[RuntimeApi, Unit] =
    ZIO.serviceWithZIO(_.sendInitializationError(errorResponse))
}
