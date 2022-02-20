package zio.lambda.internal

import zio._
import zio.lambda.Context
import zio.lambda.ZLambdaApp

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
                .foldZIO(
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
            .tapError(throwable => ZIO.attempt(println(s"Error=$throwable")))
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
    eitherZLambda: Either[Throwable, ZLambdaApp[_, _]]
  ): RIO[LoopProcessor with ZEnv, Unit] =
    ZIO.serviceWithZIO[LoopProcessor](_.loop(eitherZLambda))

  val live: ZLayer[RuntimeApi with LambdaEnvironment, Throwable, LoopProcessor] =
    (for {
      runtimeApi  <- ZIO.service[RuntimeApi]
      environment <- ZIO.service[LambdaEnvironment]
    } yield Live(runtimeApi, environment)).toLayer

}
