package zio.lambda.internal

import zio._
import zio.blocking._
import zio.lambda.internal.LambdaLoader.Error
import zio.test.Assertion._
import zio.test._

object LambdaLoaderLiveSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[Environment, Failure] =
    suite("LambdaLoaderLive unit tests")(
      testM("should return Error.UserError if taskRoot is None") {
        val lambdaEnvironmentLayer = ZLayer.succeed(
          LambdaEnvironment("", None, None, None, None, None, None, None)
        )

        val lambdaLoaderLayer = (lambdaEnvironmentLayer ++ Blocking.live) >>> LambdaLoader.layer

        LambdaLoader
          .loadLambda()
          .map(assert(_)(isLeft(equalTo(Error.userError("Task Root not defined")))))
          .provideLayer(lambdaLoaderLayer)
      },
      testM("should return Error.UserError if Function Handler is None") {
        val lambdaEnvironmentLayer = ZLayer.succeed(
          LambdaEnvironment("", None, Some("/opt"), None, None, None, None, None)
        )

        val lambdaLoaderLayer = (lambdaEnvironmentLayer ++ Blocking.live) >>> LambdaLoader.layer

        LambdaLoader
          .loadLambda()
          .map(assert(_)(isLeft(equalTo(Error.userError("Function Handler not defined")))))
          .provideLayer(lambdaLoaderLayer)
      }
      // FIXME fails with SBT
      // testM("should load ZLambda") {
      //   val lambdaEnvironmentLayer = ZLayer.succeed(
      //     LambdaEnvironment(
      //       "",
      //       Some("zio.lambda.internal.SuccessZLambda"),
      //       Some(""),
      //       None,
      //       None,
      //       None,
      //       None,
      //       None
      //     )
      //   )
      //   val lambdaLoaderLayer = (lambdaEnvironmentLayer ++ Blocking.live) >>> LambdaLoader.layer

      //   LambdaLoader
      //     .loadLambda()
      //     .flatMap {
      //       case Right(zLambda) =>
      //         zLambda.runHandler(CustomPayload("payload").toJson)
      //       case Left(error) => ZIO.fail(s"ZLambda not loaded. Error=$error")
      //     }
      //     .map(Function.const(assertCompletes))
      //     .provideCustomLayer(lambdaLoaderLayer)
      // }
    )

}
