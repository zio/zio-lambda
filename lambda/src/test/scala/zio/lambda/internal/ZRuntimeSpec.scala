package zio.lambda.internal

import zio._
import zio.json._
import zio.test.Assertion._
import zio.test._

object ZRuntimeSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[Environment, Failure] = suite("ZRuntimeLive spec")(
    testM("should process invocation and send invocation response") {
      checkM(InvocationRequestGen.gen, LambdaEnvironmentGen.gen) { (invocationRequest, lambdaEnvironment) =>
        val loopProcessorLayer =
          (TestRuntimeApi.testLayer ++
            ZLayer.succeed(lambdaEnvironment)) >>> LoopProcessor.live ++ TestRuntimeApi.testLayer

        (for {
          _ <- TestRuntimeApi.addInvocationRequest(
                 invocationRequest.copy(
                   payload = CustomPayload(invocationRequest.payload).toJson
                 )
               )
          _                      <- LoopProcessor.loop(Right(SuccessZLambda))
          invocationResponseSent <- TestRuntimeApi.getInvocationResponse()
        } yield assert(invocationResponseSent)(
          equalTo(
            InvocationResponse(
              invocationRequest.id,
              CustomResponse(invocationRequest.payload).toJson
            )
          )
        )).provideCustomLayer(loopProcessorLayer)
      }
    },
    testM("should send invocation error if ZLambda wasn't loaded successfully ") {
      checkM(InvocationRequestGen.gen, LambdaEnvironmentGen.gen) { (invocationRequest, lambdaEnvironment) =>
        val loopProcessorLayer =
          (TestRuntimeApi.testLayer ++
            ZLayer.succeed(lambdaEnvironment)) >>> LoopProcessor.live ++ TestRuntimeApi.testLayer

        (for {
          _ <- TestRuntimeApi.addInvocationRequest(
                 invocationRequest.copy(
                   payload = CustomPayload(invocationRequest.payload).toJson
                 )
               )
          loaderLambdaError = new Throwable("Error loading ZLambda")
          _                <- LoopProcessor.loop(Left(loaderLambdaError))
          invocationError  <- TestRuntimeApi.getInvocationError()

        } yield assert(invocationError)(
          equalTo(
            InvocationError(
              invocationRequest.id,
              InvocationErrorResponse.fromThrowable(loaderLambdaError)
            )
          )
        )).provideCustomLayer(loopProcessorLayer)
      }
    },
    testM("should send invocation error if ZLambda fails") {
      checkM(InvocationRequestGen.gen, LambdaEnvironmentGen.gen) { (invocationRequest, lambdaEnvironment) =>
        val loopProcessorLayer =
          (TestRuntimeApi.testLayer ++
            ZLayer.succeed(lambdaEnvironment)) >>> LoopProcessor.live ++ TestRuntimeApi.testLayer

        (for {
          _ <- TestRuntimeApi.addInvocationRequest(
                 invocationRequest.copy(
                   payload = CustomPayload(invocationRequest.payload).toJson
                 )
               )
          _ <- LoopProcessor.loop(Right(ErrorZLambda))
          _ <- TestRuntimeApi.getInvocationError()

        } yield assertCompletes).provideCustomLayer(loopProcessorLayer)
      }
    }
  )

}
