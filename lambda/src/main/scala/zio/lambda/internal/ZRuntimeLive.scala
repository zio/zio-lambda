package zio.lambda.internal

import zio._
import zio.lambda.Context
import zio.lambda.ZLambda

final case class ZRuntimeLive(runtimeApi: RuntimeApi, environment: LambdaEnvironment) extends ZRuntime {

  override def processInvocation(eitherZLambda: Either[Throwable, ZLambda[_, _]]): RIO[ZEnv, Unit] =
    runtimeApi
      .getNextInvocation()
      .flatMap(request =>
        eitherZLambda match {
          case Left(throwable) =>
            runtimeApi
              .sendInvocationError(
                InvocationError(
                  request.id,
                  InvocationErrorResponse.fromThrowable(throwable)
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

object ZRuntimeLive {
  val layer: URLayer[Has[RuntimeApi] with Has[LambdaEnvironment], Has[ZRuntime]] =
    (ZRuntimeLive(_, _)).toLayer
}
