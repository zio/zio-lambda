package zio.lambda.internal

import zio._
import zio.lambda.ZLambda

final case class ZRuntimeLive(runtimeApi: RuntimeApi) extends ZRuntime {

  override def processInvocation(eitherZLambda: Either[LambdaLoader.Error, ZLambda[_, _]]): RIO[ZEnv, Unit] =
    runtimeApi
      .getNextInvocation()
      .flatMap(request =>
        eitherZLambda match {
          case Left(error) =>
            runtimeApi
              .sendInvocationError(
                InvocationError(
                  request.id,
                  InvocationErrorResponse.fromLambdaLoaderError(error)
                )
              )
          case Right(zLambda) =>
            zLambda
              .runHandler(request.payload)
              .flatMap(payload =>
                runtimeApi.sendInvocationResponse(
                  InvocationResponse(request.id, payload)
                )
              )
              .catchAll(throwable =>
                runtimeApi.sendInvocationError(
                  InvocationError(request.id, InvocationErrorResponse.fromThrowable(throwable))
                )
              )
        }
      )

}
