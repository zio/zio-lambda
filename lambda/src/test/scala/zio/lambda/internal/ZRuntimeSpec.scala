package zio.lambda.internal

import zio._
import zio.json._
import zio.test.Assertion._
import zio.test._

object ZRuntimeSpec extends ZIOSpecDefault {

  override def spec = suite("ZRuntimeLive spec")(
    test("should process invocation and send invocation response") {
      checkN(1)(InvocationRequestGen.gen.noShrink, LambdaEnvironmentGen.gen.noShrink) {
        (invocationRequest, lambdaEnvironment) =>
          (for {
            _ <- LoopProcessor.loop(Right(SuccessZLambda)).fork

            _ <- TestRuntimeApi.addInvocationRequest(
                   invocationRequest.copy(
                     payload = CustomPayload(invocationRequest.payload).toJson
                   )
                 )

            invocationResponseSent <- TestRuntimeApi.getInvocationResponse()

          } yield assert(invocationResponseSent)(
            equalTo(
              InvocationResponse(
                invocationRequest.id,
                CustomResponse(invocationRequest.payload).toJson
              )
            )
          )).provide(
            TestRuntimeApi.testLayer,
            ZLayer.succeed(lambdaEnvironment),
            LoopProcessor.live
          )
      }
    },
    test("should send invocation error if ZLambda wasn't loaded successfully ") {
      check(InvocationRequestGen.gen, LambdaEnvironmentGen.gen) { (invocationRequest, lambdaEnvironment) =>
        val loaderLambdaError = new Throwable("Error loading ZLambda")

        (for {
          _ <- LoopProcessor.loop(Left(loaderLambdaError)).fork

          _ <- TestRuntimeApi.addInvocationRequest(
                 invocationRequest.copy(
                   payload = CustomPayload(invocationRequest.payload).toJson
                 )
               )

          invocationError <- TestRuntimeApi.getInvocationError()

        } yield assert(invocationError)(
          equalTo(
            InvocationError(
              invocationRequest.id,
              InvocationErrorResponse.fromThrowable(loaderLambdaError)
            )
          )
        )).provide(
          TestRuntimeApi.testLayer,
          ZLayer.succeed(lambdaEnvironment),
          LoopProcessor.live
        )
      }
    },
    test("should send invocation error if ZLambda fails") {
      check(InvocationRequestGen.gen, LambdaEnvironmentGen.gen) { (invocationRequest, lambdaEnvironment) =>
        val loopProcessorLayer =
          (TestRuntimeApi.testLayer ++
            ZLayer.succeed(lambdaEnvironment)) >>> LoopProcessor.live ++ TestRuntimeApi.testLayer

        (for {
          _ <- LoopProcessor.loop(Right(ErrorZLambda)).fork

          _ <- TestRuntimeApi.addInvocationRequest(
                 invocationRequest.copy(
                   payload = CustomPayload(invocationRequest.payload).toJson
                 )
               )

          _ <- TestRuntimeApi.getInvocationError()

        } yield assertCompletes).provide(loopProcessorLayer)
      }
    }
  )

}
