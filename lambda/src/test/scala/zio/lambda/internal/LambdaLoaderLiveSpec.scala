package zio.lambda.internal

import zio._
import zio.blocking._
import zio.json._
import zio.lambda.Context
import zio.test.Assertion._
import zio.test._
import zio.lambda.ClientContext
import zio.lambda.Client
import zio.lambda.CognitoIdentity

object LambdaLoaderLiveSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[Environment, Failure] =
    suite("LambdaLoaderLive spec")(
      testM("should return an error if Function Handler is None") {
        val lambdaEnvironmentLayer = ZLayer.succeed(
          LambdaEnvironment("", "non_exists", "/opt", 128, "", "", "", "")
        )

        val lambdaLoaderLayer =
          (lambdaEnvironmentLayer ++ Blocking.live ++ TestCustomClassLoader.test) >>> LambdaLoaderLive.layer

        LambdaLoader.loadLambda
          .map(assert(_)(isLeft))
          .provideLayer(lambdaLoaderLayer)
      },
      testM("should load ZLambda") {
        val lambdaEnvironmentLayer = ZLayer.succeed(
          LambdaEnvironment(
            "",
            "zio.lambda.internal.SuccessZLambda",
            "",
            0,
            "",
            "",
            "",
            ""
          )
        )

        val context = Context(
          "",
          "",
          "",
          "",
          "",
          "",
          0,
          1,
          Some(ClientContext(Client("", "", "", "", ""), Map.empty, Map.empty)),
          Some(CognitoIdentity("", ""))
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
