package zio.runtime

import zio._

final case class ZRuntimeLive(runtimeApi: RuntimeApi) extends ZRuntime {

  override def processInvocation(f: String => RIO[ZEnv, String]): RIO[ZEnv, Unit] =
    runtimeApi
      .nextInvocation()
      .flatMap(request =>
        f(request.payload)
          .flatMap(runtimeApi.invocationResponse(request.id, _))
          .tapError(throwable => runtimeApi.invocationError(request.id, InvocationError.fromThrowable(throwable)))
      )
      .forever
      .tapError(throwable => runtimeApi.initializationError(InvocationError.fromThrowable(throwable)))
      .unit

}
