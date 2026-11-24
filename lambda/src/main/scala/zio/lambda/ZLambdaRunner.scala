package zio.lambda

import zio.json.{JsonDecoder, JsonEncoder}
import zio.json._
import zio._
import zio.lambda.internal.{LambdaEnvironment, LoopProcessor, RuntimeApiLive}

/**
 * The entry point that turns a handler function into an AWS Lambda custom runtime. Return the effect of `serve` or
 * `serveRaw` from the `run` method of a `ZIOAppDefault`, and package that app as the function's executable.
 *
 * Both methods run the same loop. For each invocation it asks the Lambda Runtime API for the next event, builds a
 * [[Context]], calls the function and reports the outcome to Lambda:
 *
 *   - when the function's effect succeeds, the result is sent as the invocation response;
 *   - when it fails, the error is sent as an invocation error, and the loop waits for the next invocation.
 *
 * If the `AWS_LAMBDA_RUNTIME_API` environment variable is not set, the effect fails with a `Throwable` whose message
 * is `AWS_LAMBDA_RUNTIME_API env variable not defined`.
 *
 * {{{
 * import zio._
 * import zio.json._
 *
 * final case class Order(id: Int, quantity: Int)
 * object Order {
 *   implicit val decoder: JsonDecoder[Order] = DeriveJsonDecoder.gen[Order]
 * }
 *
 * final case class Receipt(orderId: Int, accepted: Boolean)
 * object Receipt {
 *   implicit val encoder: JsonEncoder[Receipt] = DeriveJsonEncoder.gen[Receipt]
 * }
 *
 * object OrderHandler extends ZIOAppDefault {
 *   val handle = (order: Order, context: Context) =>
 *     ZIO.logInfo(s"Request \${context.awsRequestId}").as(Receipt(order.id, order.quantity > 0))
 *
 *   override val run = ZLambdaRunner.serve(handle)
 * }
 * }}}
 *
 * @see [[https://zio.dev/zio-lambda/ ZIO Lambda]]
 */
object ZLambdaRunner {

  /**
   * Serves Lambda invocations with a typed function. For each invocation it decodes the event JSON into `IN` with the
   * implicit `JsonDecoder`, calls `appFunction`, and encodes the `OUT` result with the implicit `JsonEncoder`.
   *
   * When the event cannot be decoded, the invocation fails with a `Throwable` whose message starts with
   * `Error decoding json.`. The loop is described on [[ZLambdaRunner]].
   *
   * {{{
   * import zio._
   *
   * object LengthHandler extends ZIOAppDefault {
   *   val length = (text: String, _: Context) => ZIO.succeed(text.length)
   *
   *   override val run = ZLambdaRunner.serve(length)
   * }
   * }}}
   *
   * @tparam R the environment that `appFunction` needs; the caller provides it
   * @tparam ERR the error type of `appFunction`, a subtype of `Throwable`
   */
  def serve[R, IN: JsonDecoder, OUT: JsonEncoder, ERR <: Throwable](
    appFunction: (IN, Context) => ZIO[R, ERR, OUT]
  ): RIO[R, Unit] =
    LoopProcessor
      .loopZioApp(Right(defaultRaw[R, IN, OUT, ERR](_, _, appFunction)))
      .provideSomeLayer[R](ZLambdaRunner.default)

  /**
   * Serves Lambda invocations with a function on raw strings. It passes the event payload to `rawFunction` as it
   * arrives, with no decoding, and sends the returned `String` to Lambda as it is, so the function must return valid
   * JSON when the caller expects JSON. The loop is described on [[ZLambdaRunner]].
   *
   * {{{
   * import zio._
   *
   * object EchoHandler extends ZIOAppDefault {
   *   val echo = (payload: String, _: Context) => ZIO.succeed(payload)
   *
   *   override val run = ZLambdaRunner.serveRaw(echo)
   * }
   * }}}
   */
  def serveRaw[R](rawFunction: (String, Context) => ZIO[R, Throwable, String]): RIO[R, Unit] =
    LoopProcessor
      .loopZioApp(Right(rawFunction))
      .provideSomeLayer[R](ZLambdaRunner.default)

  private def defaultRaw[R, IN: JsonDecoder, OUT: JsonEncoder, ERR <: Throwable](
    json: String,
    context: Context,
    userFunction: (IN, Context) => ZIO[R, ERR, OUT]
  ): ZIO[R, Throwable, String] =
    JsonDecoder[IN].decodeJson(json) match {
      case Left(errorMessage) =>
        ZIO.fail(new Throwable(s"Error decoding json. Json=$json, Error$errorMessage"))
      case Right(event) =>
        userFunction(event, context).map(_.toJson)
    }

  private def default =
    LambdaEnvironment.live >>> (RuntimeApiLive.layer ++ LambdaEnvironment.live) >>> LoopProcessor.live

}
