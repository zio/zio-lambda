package zio.lambda.internal

import sttp.client3._
import sttp.client3.testing._
import sttp.model._
import zio._
import zio.blocking._
import zio.json._
import zio.test.Assertion._
import zio.test._

import scala.collection.immutable.Seq

import InvocationRequestImplicits._

object RuntimeApiLiveSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[Environment, Failure] =
    suite("RuntimeApiLive spec")(
      testM("should get next invocation") {
        checkM(InvocationRequestGen.gen, LambdaEnvironmentGen.gen) { (invocationRequest, lambdaEnvironment) =>
          val testingBackend: SttpBackend[Identity, Any] = SttpBackendStub.synchronous
            .whenRequestMatches(request =>
              request.uri == uri"http://${lambdaEnvironment.runtimeApi}/2018-06-01/runtime/invocation/next" &&
                request.method == Method.GET
            )
            .thenRespond(
              Response(
                body = invocationRequest.payload,
                code = StatusCode.Ok,
                statusText = "",
                headers = Seq(
                  Option(Header("Lambda-Runtime-Aws-Request-Id", invocationRequest.id.value)),
                  invocationRequest.remainingTimeInMillis
                    .map(remainingTimeInMillis =>
                      Header("Lambda-Runtime-Deadline-Ms", remainingTimeInMillis.toString())
                    ),
                  invocationRequest.invokedFunctionArn
                    .map(invokedFunctionArn => Header("Lambda-Runtime-Invoked-Function-Arn", invokedFunctionArn)),
                  invocationRequest.xrayTraceId.map(xrayTraceId => Header("Lambda-Runtime-Trace-Id", xrayTraceId)),
                  invocationRequest.clientContext
                    .map(clientContext => Header("Lambda-Runtime-Client-Context", clientContext.toJson)),
                  invocationRequest.cognitoIdentity
                    .map(cognitoIdentity => Header("Lambda-Runtime-Cognito-Identity", cognitoIdentity.toJson))
                ).flatten
              )
            )

          val runtimeApiLayer = (ZLayer.succeed(lambdaEnvironment) ++
            Blocking.live ++
            ZLayer.succeed(testingBackend)) >>> RuntimeApiLive.layer

          RuntimeApi.getNextInvocation
            .provideLayer(runtimeApiLayer)
            .map(assert(_)(equalTo(invocationRequest)))
        }
      },
      testM("should send invocation response") {
        checkM(Gen.alphaNumericString, LambdaEnvironmentGen.gen, Gen.anyString) {
          (requestId, lambdaEnvironment, payload) =>
            val testingBackend: SttpBackend[Identity, Any] = SttpBackendStub.synchronous
              .whenRequestMatches(request =>
                request.uri == uri"http://${lambdaEnvironment.runtimeApi}/2018-06-01/runtime/invocation/$requestId/response" &&
                  request.method == Method.POST &&
                  request.body == StringBody(payload, "utf-8")
              )
              .thenRespondOk()

            val runtimeApiLayer = (ZLayer.succeed(lambdaEnvironment) ++
              Blocking.live ++
              ZLayer.succeed(testingBackend)) >>> RuntimeApiLive.layer

            RuntimeApi
              .sendInvocationResponse(
                InvocationResponse(InvocationRequest.Id(requestId), payload)
              )
              .provideLayer(runtimeApiLayer)
              .map(Function.const(assertCompletes))
        }
      },
      testM("should send invocation error") {
        checkM(LambdaEnvironmentGen.gen, InvocationErrorGen.gen) { (lambdaEnvironment, invocationError) =>
          val testingBackend: SttpBackend[Identity, Any] = SttpBackendStub.synchronous
            .whenRequestMatches(request =>
              request.uri == uri"http://${lambdaEnvironment.runtimeApi}/2018-06-01/runtime/invocation/${invocationError.requestId.value}/error" &&
                request.method == Method.POST &&
                request.body == StringBody(invocationError.errorResponse.toJson, "utf-8")
            )
            .thenRespondOk()

          val runtimeApiLayer = (ZLayer.succeed(lambdaEnvironment) ++
            Blocking.live ++
            ZLayer.succeed(testingBackend)) >>> RuntimeApiLive.layer

          RuntimeApi
            .sendInvocationError(invocationError)
            .provideLayer(runtimeApiLayer)
            .map(Function.const(assertCompletes))
        }
      },
      testM("should send initialization error") {
        checkM(LambdaEnvironmentGen.gen, InvocationErrorGen.gen) { (lambdaEnvironment, invocationError) =>
          val testingBackend: SttpBackend[Identity, Any] = SttpBackendStub.synchronous
            .whenRequestMatches(request =>
              request.uri == uri"http://${lambdaEnvironment.runtimeApi}/2018-06-01/runtime/init/error" &&
                request.method == Method.POST &&
                request.body == StringBody(invocationError.errorResponse.toJson, "utf-8") &&
                request.headers
                  .exists(_ == Header("Lambda-Runtime-Function-Error-Type", invocationError.errorResponse.errorType))
            )
            .thenRespondOk()

          val runtimeApiLayer = (ZLayer.succeed(lambdaEnvironment) ++
            Blocking.live ++
            ZLayer.succeed(testingBackend)) >>> RuntimeApiLive.layer

          RuntimeApi
            .sendInitializationError(invocationError.errorResponse)
            .provideLayer(runtimeApiLayer)
            .map(Function.const(assertCompletes))
        }
      }
    )

}
