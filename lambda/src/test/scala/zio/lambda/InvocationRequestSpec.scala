package zio.lambda

import zio.json._
import zio.test.Assertion._
import zio.test._

import InvocationRequestImplicits._

object InvocationRequestSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[Environment, Failure] =
    suite("InvocationRequest unit tests")(
      suite("fromHttpResponse")(
        testM("should return Left if Lambda-Runtime-Aws-Request-Id header is missing") {
          check(Gen.anyString) { payload =>
            assert(
              InvocationRequest.fromHttpResponse(Map.empty, payload)
            )(isLeft(equalTo("Lambda-Runtime-Aws-Request-Id is missing")))
          }
        },
        testM("should return Left if Lambda-Runtime-Deadline-Ms header is missing") {
          check(Gen.anyString, Gen.anyString) { (requestId, payload) =>
            assert(
              InvocationRequest.fromHttpResponse(
                Map("Lambda-Runtime-Aws-Request-Id" -> requestId),
                payload
              )
            )(isLeft(equalTo("Lambda-Runtime-Deadline-Ms is missing")))
          }
        },
        testM("should return Left if Lambda-Runtime-Invoked-Function-Arn header is missing") {
          check(Gen.anyString, Gen.anyLong, Gen.anyString) { (requestId, deadLineMs, payload) =>
            assert(
              InvocationRequest.fromHttpResponse(
                Map(
                  "Lambda-Runtime-Aws-Request-Id" -> requestId,
                  "Lambda-Runtime-Deadline-Ms"    -> deadLineMs.toString()
                ),
                payload
              )
            )(isLeft(equalTo("Lambda-Runtime-Invoked-Function-Arn is missing")))
          }
        },
        testM("should return Left if Lambda-Runtime-Trace-Id header is missing") {
          check(Gen.anyString, Gen.anyLong, Gen.anyString, Gen.anyString) {
            (requestId, deadLineMs, invokedFunctionArn, payload) =>
              assert(
                InvocationRequest.fromHttpResponse(
                  Map(
                    "Lambda-Runtime-Aws-Request-Id"       -> requestId,
                    "Lambda-Runtime-Deadline-Ms"          -> deadLineMs.toString(),
                    "Lambda-Runtime-Invoked-Function-Arn" -> invokedFunctionArn
                  ),
                  payload
                )
              )(isLeft(equalTo("Lambda-Runtime-Trace-Id is missing")))
          }
        },
        testM("should return InvocationRequest") {
          check(InvocationRequestGen.gen) { invocationRequest =>
            assert(
              InvocationRequest.fromHttpResponse(
                Map(
                  "Lambda-Runtime-Aws-Request-Id"       -> invocationRequest.id.value,
                  "Lambda-Runtime-Deadline-Ms"          -> invocationRequest.deadlineMs.toString(),
                  "Lambda-Runtime-Invoked-Function-Arn" -> invocationRequest.invokedFunctionArn,
                  "Lambda-Runtime-Trace-Id"             -> invocationRequest.xrayTraceId
                ) ++
                  invocationRequest.clientContext
                    .map(clientContext => ("Lambda-Runtime-Client-Context" -> clientContext.toJson))
                    .toMap
                  ++
                  invocationRequest.cognitoIdentity
                    .map(cognitoIdentity => ("Lambda-Runtime-Cognito-Identity" -> cognitoIdentity.toJson))
                    .toMap,
                invocationRequest.payload
              )
            )(isRight(equalTo(invocationRequest)))
          }
        }
      )
    )

}
