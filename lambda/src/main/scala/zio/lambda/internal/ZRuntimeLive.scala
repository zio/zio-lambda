package zio.lambda.internal

import zio._
import zio.lambda.ZLambda
import zio.lambda.Context

final case class ZRuntimeLive(runtimeApi: RuntimeApi, environment: LambdaEnvironment) extends ZRuntime {

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
              .runHandler(request.payload, Context.from(request, environment))
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
