package zio.lambda.internal

import zio._
import zio.lambda.{Context, ZLambda}

trait LoopProcessor {
  @deprecated("Use loopZioApp", "1.0.3")
  def loop(eitherZLambda: Either[Throwable, ZLambda[_, _]]): Task[Unit]
  def loopZioApp[R](rawFunction: Either[Throwable, (String, Context) => ZIO[R, Throwable, String]]): RIO[R, Unit]
}

object LoopProcessor {

  final case class Live(runtimeApi: RuntimeApi, environment: LambdaEnvironment) extends LoopProcessor {

    @deprecated("Use loopZioApp", "1.0.3")
    def loop(eitherZLambda: Either[Throwable, ZLambda[_, _]]): Task[Unit] =
      loopZioApp(eitherZLambda.map(_.applyJson))

    def loopZioApp[R](rawFunction: Either[Throwable, (String, Context) => ZIO[R, Throwable, String]]): RIO[R, Unit] =
      rawFunction match {
        case Right(zLambda) =>
          runtimeApi.getNextInvocation
            .flatMap[R, Throwable, Unit](request =>
              zLambda(request.payload, Context.from(request, environment))
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

  @deprecated("Use loopZioApp", "1.0.3")
  def loop(
    eitherZLambda: Either[Throwable, ZLambda[_, _]]
  ): RIO[LoopProcessor, Unit] =
    ZIO.serviceWithZIO[LoopProcessor](_.loop(eitherZLambda))

  def loopZioApp[R](
    rawFunction: Either[Throwable, (String, Context) => ZIO[R, Throwable, String]]
  ): RIO[LoopProcessor & R, Unit] =
    ZIO.serviceWithZIO[LoopProcessor](_.loopZioApp(rawFunction))

  val live: ZLayer[RuntimeApi with LambdaEnvironment, Throwable, LoopProcessor] =
    ZLayer {
      for {
        runtimeApi  <- ZIO.service[RuntimeApi]
        environment <- ZIO.service[LambdaEnvironment]
      } yield Live(runtimeApi, environment)
    }

}
