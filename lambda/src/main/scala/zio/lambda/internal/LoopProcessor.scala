package zio.lambda.internal

import zio._
import zio.lambda.ZLambdaApp
import zio.lambda.Context

trait LoopProcessor {
  def loop(eitherZLambda: Either[Throwable, ZLambdaApp[_, _]]): RIO[ZEnv, Unit]
}

object LoopProcessor {

  final case class Live(runtimeApi: RuntimeApi, environment: LambdaEnvironment) extends LoopProcessor {
    def loop(eitherZLambda: Either[Throwable, ZLambdaApp[_, _]]): RIO[ZEnv, Unit] =
      eitherZLambda match {
        case Right(zLambda) =>
          runtimeApi.getNextInvocation
            .flatMap(request =>
              zLambda
                .applyJson(request.payload, Context.from(request, environment))
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
            )

        case Left(throwable) =>
          runtimeApi.getNextInvocation
            .flatMap[Any, Throwable, Unit](request =>
              runtimeApi
                .sendInvocationError(
                  InvocationError(
                    request.id,
                    InvocationErrorResponse.fromThrowable(throwable)
                  )
                )
            )
      }
  }

  def loop(eitherZLambda: Either[Throwable, ZLambdaApp[_, _]]): RIO[Has[LoopProcessor] with ZEnv, Unit] =
    ZIO.accessM(_.get.loop(eitherZLambda))

  val live: ZLayer[Has[RuntimeApi] with Has[LambdaEnvironment], Throwable, Has[LoopProcessor]] =
    (for {
      runtimeApi  <- RuntimeApi.getRuntimeApi
      environment <- LambdaEnvironment.getEnvironment
    } yield Live(runtimeApi, environment)).toLayer

}
