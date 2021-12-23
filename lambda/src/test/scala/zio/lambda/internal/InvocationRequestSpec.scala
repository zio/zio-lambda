package zio.lambda.internal

import zio.json._
import zio.test.Assertion._
import zio.test._

import InvocationRequestImplicits._

object InvocationRequestSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[Environment, Failure] =
    suite("InvocationRequest spec")(
      suite("fromHttpResponse")(
        testM("should return Left if Lambda-Runtime-Aws-Request-Id header is missing") {
          check(Gen.anyString) { payload =>
            assert(
              InvocationRequest.fromHttpResponse(Map.empty, payload)
            )(isLeft(equalTo("Lambda-Runtime-Aws-Request-Id is missing")))
          }
        },
        testM("should return InvocationRequest") {
          check(InvocationRequestGen.gen) { invocationRequest =>
            assert(
              InvocationRequest.fromHttpResponse(
                Seq(
                  Option("Lambda-Runtime-Aws-Request-Id" -> invocationRequest.id.value),
                  invocationRequest.xrayTraceId.map(xrayTraceId => "Lambda-Runtime-Trace-Id" -> xrayTraceId),
                  invocationRequest.invokedFunctionArn.map(invocakedFunctionArn =>
                    "Lambda-Runtime-Invoked-Function-Arn" -> invocakedFunctionArn
                  ),
                  invocationRequest.remainingTimeInMillis.map(remainingTimeInMillis =>
                    ("Lambda-Runtime-Deadline-Ms" -> remainingTimeInMillis.toString())
                  ),
                  invocationRequest.clientContext
                    .map(clientContext => ("Lambda-Runtime-Client-Context" -> clientContext.toJson)),
                  invocationRequest.cognitoIdentity
                    .map(cognitoIdentity => ("Lambda-Runtime-Cognito-Identity" -> cognitoIdentity.toJson))
                ).flatten.toMap,
                invocationRequest.payload
              )
            )(isRight(equalTo(invocationRequest)))
          }
        }
      )
    )

}
