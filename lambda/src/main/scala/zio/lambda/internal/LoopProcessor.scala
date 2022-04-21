package zio.lambda.internal

import zio._
import zio.lambda.Context
import zio.lambda.ZLambda

trait LoopProcessor {
  def loop(eitherZLambda: Either[Throwable, ZLambda[_, _]]): Task[Unit]
}

object LoopProcessor {

  final case class Live(runtimeApi: RuntimeApi, environment: LambdaEnvironment) extends LoopProcessor {
    def loop(eitherZLambda: Either[Throwable, ZLambda[_, _]]): Task[Unit] =
      eitherZLambda match {
        case Right(zLambda) =>
          runtimeApi.getNextInvocation
            .flatMap(request =>
              zLambda
                .applyJson(request.payload, Context.from(request, environment))
                .foldZIO[Any, Throwable, Unit](
                  throwable =>
                    runtimeApi.sendInvocationError(
                      InvocationError(request.id, InvocationErrorResponse.fromThrowable(throwable))
                    ),
                  payload =>
                    runtimeApi.sendInvocationResponse(
                      InvocationResponse(request.id, payload)
                    )
                )
            )
            .forever

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

  def loop(
    eitherZLambda: Either[Throwable, ZLambda[_, _]]
  ): RIO[LoopProcessor, Unit] =
    ZIO.serviceWithZIO[LoopProcessor](_.loop(eitherZLambda))

  val live: ZLayer[RuntimeApi with LambdaEnvironment, Throwable, LoopProcessor] =
    ZLayer {
      for {
        runtimeApi  <- ZIO.service[RuntimeApi]
        environment <- ZIO.service[LambdaEnvironment]
      } yield Live(runtimeApi, environment)
    }

}
