package zio.lambda

import zio._
import zio.json._
import zio.lambda.internal.LambdaEnvironment
import zio.lambda.internal.LoopProcessor
import zio.lambda.internal.RuntimeApiLive

/**
 * A Lambda handler written as a `ZIOAppDefault` subclass: implement `apply`, and `run` serves invocations with it.
 * Deprecated since 1.0.3: pass the same function to [[ZLambdaRunner.serve]] from the `run` method of a
 * `ZIOAppDefault` instead.
 *
 * `run` runs the same invocation loop as [[ZLambdaRunner]], with the same handling of failures and decoding errors.
 *
 * @tparam E the event type, decoded from the invocation payload with the implicit `JsonDecoder`
 * @tparam A the response type, encoded with the implicit `JsonEncoder`
 */
@deprecated("Use ZLambdaRunner", "1.0.3")
abstract class ZLambda[E: JsonDecoder, A: JsonEncoder] extends ZIOAppDefault { self =>

  /** Handles one invocation: returns the response for `event`, or fails to report an invocation error. */
  def apply(event: E, context: Context): Task[A]

  /**
   * Decodes `json` into `E`, calls [[apply]], and encodes the result. When `json` cannot be decoded, it fails with a
   * `Throwable` whose message starts with `Error decoding json.`.
   */
  def applyJson(json: String, context: Context): Task[String] =
    JsonDecoder[E].decodeJson(json) match {
      case Left(errorMessage) =>
        ZIO.fail(new Throwable(s"Error decoding json. Json=$json, Error$errorMessage"))

      case Right(event) => apply(event, context).map(_.toJson)
    }

  /** Serves Lambda invocations with [[applyJson]] as described on [[ZLambdaRunner]]. */
  def run =
    LoopProcessor
      .loop(Right(self))
      .provide(
        LambdaEnvironment.live,
        RuntimeApiLive.layer,
        LoopProcessor.live
      )

}
