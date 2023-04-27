package zio.lambda.internal

import zio._
import zio.json._
import zio.lambda.Context
import zio.test.Assertion._
import zio.test._
import zio.lambda.ClientContext
import zio.lambda.Client
import zio.lambda.CognitoIdentity

object LambdaLoaderLiveSpec extends ZIOSpecDefault {

  override def spec =
    suite("LambdaLoaderLive spec")(
      test("should return an error if Function Handler is None") {
        val lambdaEnvironmentLayer = ZLayer.succeed(
          LambdaEnvironment("", "non_exists", "/opt", 128, "", "", "", "")
        )

        LambdaLoader.loadLambdaApp
          .map(assert(_)(isLeft))
          .provide(
            lambdaEnvironmentLayer,
            TestCustomClassLoader.test,
            LambdaAppLoaderLive.layer
          )
      },
      test("should load ZLambda") {
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

        LambdaLoader.loadLambda.flatMap {
          case Right(zLambda) =>
            zLambda.applyJson(CustomPayload("payload").toJson, context)
          case Left(error) => ZIO.fail(s"ZLambda not loaded. Error=$error")
        }
          .map(Function.const(assertCompletes))
          .provide(
            lambdaEnvironmentLayer,
            TestCustomClassLoader.test,
            LambdaLoaderLive.layer
          )
      }
    )

}
