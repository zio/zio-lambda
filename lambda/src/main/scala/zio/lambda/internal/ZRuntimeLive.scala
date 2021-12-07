package zio.lambda.internal

import zio._
import zio.lambda.Context
import zio.lambda.ZLambdaApp

final case class ZRuntimeLive(runtimeApi: RuntimeApi, environment: LambdaEnvironment) extends ZRuntime {

  override def processInvocation(
    eitherZLambda: Either[Throwable, ZLambdaApp[_, _]]
  ): RIO[ZEnv, Unit] =
    runtimeApi
      .getNextInvocation()
      .flatMap(request =>
        eitherZLambda.fold(
          throwable =>
            runtimeApi
              .sendInvocationError(
                InvocationError(
                  request.id,
                  InvocationErrorResponse.fromThrowable(throwable)
                )
              ),
          _.applyJson(request.payload)
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
            .provideCustomLayer(ZLayer.succeed(Context.from(request, environment)))
        )
      )
}

object ZRuntimeLive {
  val layer: URLayer[Has[RuntimeApi] with Has[LambdaEnvironment], Has[ZRuntime]] =
    (ZRuntimeLive(_, _)).toLayer
}
