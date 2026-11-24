package zio.lambda.internal

import zio._
import zio.lambda.{Context, ZLambda}

/** A service that runs the invocation loop. */
trait LoopProcessor {

  /** Runs the loop with the `applyJson` method of a deprecated `ZLambda`. */
  @deprecated("Use loopZioApp", "1.0.3")
  def loop(eitherZLambda: Either[Throwable, ZLambda[_, _]]): Task[Unit]

  /**
   * Runs the invocation loop with `rawFunction`. With a `Left`, it answers the next invocation with that error.
   */
  def loopZioApp[R](rawFunction: Either[Throwable, (String, Context) => ZIO[R, Throwable, String]]): RIO[R, Unit]
}

/** The live [[LoopProcessor]] and its accessors. */
object LoopProcessor {

  /** The [[LoopProcessor]] that talks to Lambda through a [[RuntimeApi]]. */
  final case class Live(runtimeApi: RuntimeApi, environment: LambdaEnvironment) extends LoopProcessor {

    /** Runs the loop with the `applyJson` method of a deprecated `ZLambda`. */
    @deprecated("Use loopZioApp", "1.0.3")
    def loop(eitherZLambda: Either[Throwable, ZLambda[_, _]]): Task[Unit] =
      loopZioApp(eitherZLambda.map(_.applyJson))

    /**
     * Runs the invocation loop with `rawFunction`. With a `Left`, it answers the next invocation with that error.
     */
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

  /** Runs the loop of the [[LoopProcessor]] service with a deprecated `ZLambda`. */
  @deprecated("Use loopZioApp", "1.0.3")
  def loop(
    eitherZLambda: Either[Throwable, ZLambda[_, _]]
  ): RIO[LoopProcessor, Unit] =
    ZIO.serviceWithZIO[LoopProcessor](_.loop(eitherZLambda))

  /** Runs the loop of the [[LoopProcessor]] service with `rawFunction`. */
  def loopZioApp[R](
    rawFunction: Either[Throwable, (String, Context) => ZIO[R, Throwable, String]]
  ): RIO[LoopProcessor & R, Unit] =
    ZIO.serviceWithZIO[LoopProcessor](_.loopZioApp(rawFunction))

  /** Builds a [[Live]] processor from a [[RuntimeApi]] and a [[LambdaEnvironment]]. */
  val live: ZLayer[RuntimeApi with LambdaEnvironment, Throwable, LoopProcessor] =
    ZLayer {
      for {
        runtimeApi  <- ZIO.service[RuntimeApi]
        environment <- ZIO.service[LambdaEnvironment]
      } yield Live(runtimeApi, environment)
    }

}
