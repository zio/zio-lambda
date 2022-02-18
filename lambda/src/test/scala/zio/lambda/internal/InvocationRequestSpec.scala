package zio.lambda.internal

import zio.json._
import zio.test.Assertion._
import zio.test._

import InvocationRequestImplicits._

object InvocationRequestSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[Environment, Failure] =
    suite("InvocationRequest spec")(
      suite("fromHttpResponse")(
        testM("should return InvocationRequest") {
          check(InvocationRequestGen.gen) { invocationRequest =>
            val headers = new java.util.HashMap[String, java.util.List[String]]()
            headers.put("Lambda-Runtime-Aws-Request-Id", java.util.Collections.singletonList(invocationRequest.id))
            headers.put("Lambda-Runtime-Trace-Id", java.util.Collections.singletonList(invocationRequest.xrayTraceId))
            headers.put(
              "Lambda-Runtime-Invoked-Function-Arn",
              java.util.Collections.singletonList(invocationRequest.invokedFunctionArn)
            )
            headers.put(
              "Lambda-Runtime-Deadline-Ms",
              java.util.Collections.singletonList(invocationRequest.remainingTimeInMillis.toString())
            )
            headers.put(
              "Lambda-Runtime-Client-Context",
              java.util.Collections.singletonList(invocationRequest.clientContext.toJson)
            )
            headers.put(
              "Lambda-Runtime-Cognito-Identity",
              java.util.Collections.singletonList(invocationRequest.cognitoIdentity.toJson)
            )

            assert(
              InvocationRequest.fromHttpResponse(
                headers,
                invocationRequest.payload
              )
            )(equalTo(invocationRequest))
          }
        }
      )
    )

}
