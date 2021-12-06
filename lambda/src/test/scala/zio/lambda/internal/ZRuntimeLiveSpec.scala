package zio.lambda.internal

import zio._
import zio.json._
import zio.test.Assertion._
import zio.test._

object ZRuntimeLiveSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[Environment, Failure] = suite("ZRuntimeLive spec")(
    testM("should process invocation and send invocation response") {
      checkM(InvocationRequestGen.gen, LambdaEnvironmentGen.gen) { (invocationRequest, lambdaEnvironment) =>
        val zRuntimeLayer =
          (TestRuntimeApi.testLayer ++ ZLayer.succeed(
            lambdaEnvironment
          )) >>> ZRuntimeLive.layer ++ TestRuntimeApi.testLayer

        (for {
          _ <- TestRuntimeApi.addInvocationRequest(
                 invocationRequest.copy(
                   payload = CustomPayload(invocationRequest.payload).toJson
                 )
               )
          _                      <- ZRuntime.processInvocation(Right(SuccessZLambda))
          invocationResponseSent <- TestRuntimeApi.getInvocationResponse()
        } yield assert(invocationResponseSent)(
          equalTo(
            InvocationResponse(
              invocationRequest.id,
              CustomResponse(invocationRequest.payload).toJson
            )
          )
        )).provideCustomLayer(zRuntimeLayer)
      }
    },
    testM("should send invocation error if ZLambda wasn't loaded successfully ") {
      checkM(InvocationRequestGen.gen, LambdaEnvironmentGen.gen) { (invocationRequest, lambdaEnvironment) =>
        val zRuntimeLayer =
          (TestRuntimeApi.testLayer ++ ZLayer.succeed(
            lambdaEnvironment
          )) >>> ZRuntimeLive.layer ++ TestRuntimeApi.testLayer

        (for {
          _ <- TestRuntimeApi.addInvocationRequest(
                 invocationRequest.copy(
                   payload = CustomPayload(invocationRequest.payload).toJson
                 )
               )
          loaderLambdaError = new Throwable("Error loading ZLambda")
          _                <- ZRuntime.processInvocation(Left(loaderLambdaError))
          invocationError  <- TestRuntimeApi.getInvocationError()

        } yield assert(invocationError)(
          equalTo(
            InvocationError(
              invocationRequest.id,
              InvocationErrorResponse.fromThrowable(loaderLambdaError)
            )
          )
        )).provideCustomLayer(zRuntimeLayer)
      }
    },
    testM("should send invocation error if ZLambda fails") {
      checkM(InvocationRequestGen.gen, LambdaEnvironmentGen.gen) { (invocationRequest, lambdaEnvironment) =>
        val zRuntimeLayer =
          (TestRuntimeApi.testLayer ++ ZLayer.succeed(
            lambdaEnvironment
          )) >>> ZRuntimeLive.layer ++ TestRuntimeApi.testLayer

        (for {
          _ <- TestRuntimeApi.addInvocationRequest(
                 invocationRequest.copy(
                   payload = CustomPayload(invocationRequest.payload).toJson
                 )
               )
          _ <- ZRuntime.processInvocation(Right(ErrorZLambda))
          _ <- TestRuntimeApi.getInvocationError()

        } yield assertCompletes).provideCustomLayer(zRuntimeLayer)
      }
    }
  )

}
