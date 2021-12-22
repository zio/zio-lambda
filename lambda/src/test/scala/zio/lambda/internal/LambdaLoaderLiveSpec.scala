package zio.lambda.internal

import zio._
import zio.blocking._
import zio.json._
import zio.lambda.Context
import zio.test.Assertion._
import zio.test._

object LambdaLoaderLiveSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[Environment, Failure] =
    suite("LambdaLoaderLive spec")(
      testM("should return an error if Function Handler is None") {
        val lambdaEnvironmentLayer = ZLayer.succeed(
          LambdaEnvironment("", None, Some("/opt"), 128, None, None, None, None)
        )

        val lambdaLoaderLayer =
          (lambdaEnvironmentLayer ++ Blocking.live ++ TestCustomClassLoader.test) >>> LambdaLoaderLive.layer

        LambdaLoader.loadLambda
          .map(throwable => assert(throwable.left.map(_.getMessage()))(isLeft(equalTo("Function Handler not defined"))))
          .provideLayer(lambdaLoaderLayer)
      },
      testM("should load ZLambda") {
        val lambdaEnvironmentLayer = ZLayer.succeed(
          LambdaEnvironment(
            "",
            Some("zio.lambda.internal.SuccessZLambda"),
            Some(""),
            0,
            None,
            None,
            None,
            None
          )
        )

        val context = Context(
          "",
          None,
          None,
          None,
          None,
          None,
          None,
          1,
          None,
          None
        )

        val lambdaLoaderLayer =
          (lambdaEnvironmentLayer ++ Blocking.live ++ TestCustomClassLoader.test) >>> LambdaLoaderLive.layer

        LambdaLoader.loadLambda.flatMap {
          case Right(zLambda) =>
            zLambda.applyJson(CustomPayload("payload").toJson, context)
          case Left(error) => ZIO.fail(s"ZLambda not loaded. Error=$error")
        }
          .map(Function.const(assertCompletes))
          .provideCustomLayer(lambdaLoaderLayer ++ ZLayer.succeed(context))
      }
    )

}
