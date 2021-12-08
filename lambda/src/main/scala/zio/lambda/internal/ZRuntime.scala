package zio.lambda.internal

import zio._
import zio.lambda.ZLambdaApp
import zio.lambda.Context

object ZRuntime {

  def processInvocation(
    eitherZLambda: Either[Throwable, ZLambdaApp[_, _]]
  ): RIO[ZEnv with Has[RuntimeApi] with Has[LambdaEnvironment], Unit] =
    LambdaEnvironment.getEnvironment.flatMap(environment =>
      RuntimeApi
        .getNextInvocation()
        .flatMap(request =>
          eitherZLambda.fold(
            throwable =>
              RuntimeApi
                .sendInvocationError(
                  InvocationError(
                    request.id,
                    InvocationErrorResponse.fromThrowable(throwable)
                  )
                ),
            _.applyJson(request.payload)
              .flatMap(payload =>
                RuntimeApi.sendInvocationResponse(
                  InvocationResponse(request.id, payload)
                )
              )
              .catchAll(throwable =>
                RuntimeApi.sendInvocationError(
                  InvocationError(request.id, InvocationErrorResponse.fromThrowable(throwable))
                )
              )
              .provideSomeLayer[ZEnv with Has[RuntimeApi] with Has[LambdaEnvironment]](
                ZLayer.succeed(Context.from(request, environment))
              )
          )
        )
    )

}
