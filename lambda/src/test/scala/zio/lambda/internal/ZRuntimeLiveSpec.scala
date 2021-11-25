package zio.lambda.internal

import zio.test._
import zio.test.Assertion._
import zio.json._

object ZRuntimeLiveSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[Environment, Failure] = suite("ZRuntimeLive unit tests")(
    testM("should process invocation and send invocation response") {
      checkM(InvocationRequestGen.gen) { invocationRequest =>
        val zRuntimeLayer = (TestRuntimeApi.testLayer >>> ZRuntime.layer) ++ TestRuntimeApi.testLayer

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
      checkM(InvocationRequestGen.gen) { invocationRequest =>
        val zRuntimeLayer = (TestRuntimeApi.testLayer >>> ZRuntime.layer) ++ TestRuntimeApi.testLayer

        (for {
          _ <- TestRuntimeApi.addInvocationRequest(
                 invocationRequest.copy(
                   payload = CustomPayload(invocationRequest.payload).toJson
                 )
               )
          loaderLambdaError = LambdaLoader.Error.zLambdaNotFound("Error loading ZLambda")
          _                <- ZRuntime.processInvocation(Left(loaderLambdaError))
          invocationError  <- TestRuntimeApi.getInvocationError()

        } yield assert(invocationError)(
          equalTo(
            InvocationError(
              invocationRequest.id,
              InvocationErrorResponse.fromLambdaLoaderError(loaderLambdaError)
            )
          )
        )).provideCustomLayer(zRuntimeLayer)
      }
    },
    testM("should send invocation error if ZLambda fails") {
      checkM(InvocationRequestGen.gen) { invocationRequest =>
        val zRuntimeLayer = (TestRuntimeApi.testLayer >>> ZRuntime.layer) ++ TestRuntimeApi.testLayer

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
