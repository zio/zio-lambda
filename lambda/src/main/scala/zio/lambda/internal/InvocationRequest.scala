package zio.lambda.internal

import zio.json._
import zio.lambda.ClientContext
import zio.lambda.CognitoIdentity

import scala.util.Try

/**
 * https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html
 *
 * @param requestId The request ID, which identifies the request that triggered the function invocation.
 * @param deadlineMs The date that the function times out in Unix time milliseconds.
 * @param invokedFunctionArn The ARN of the Lambda function, version, or alias that's specified in the invocation.
 * @param xrayTraceId The AWS X-Ray tracing header.
 * @param clientContext For invocations from the AWS Mobile SDK, data about the client application and device.
 * @param cognitoIdentity For invocations from the AWS Mobile SDK, data about the Amazon Cognito identity provider.
 * @param payload The payload from the invocation, which is a JSON document that contains event data from the function trigger.
 */
final case class InvocationRequest(
  id: InvocationRequest.Id,
  remainingTimeInMillis: Option[Int],
  invokedFunctionArn: Option[String],
  xrayTraceId: Option[String],
  clientContext: Option[ClientContext],
  cognitoIdentity: Option[CognitoIdentity],
  payload: String
)

object InvocationRequest {

  final case class Id(value: String) extends AnyVal

  def fromHttpResponse(headers: Map[String, String], payload: String): Either[String, InvocationRequest] =
    headers
      .get("Lambda-Runtime-Aws-Request-Id")
      .toRight("Lambda-Runtime-Aws-Request-Id is missing")
      .map { requestId =>
        val clientContext = headers
          .get("Lambda-Runtime-Client-Context")
          .flatMap(_.fromJson[ClientContext].toOption)

        val cognitoIdentity =
          headers
            .get("Lambda-Runtime-Cognito-Identity")
            .flatMap(_.fromJson[CognitoIdentity].toOption)

        val remainingTimeInMillis = headers
          .get("Lambda-Runtime-Deadline-Ms")
          .flatMap(s => Try(s.toInt).toOption)

        val invokedFunctionArn = headers.get("Lambda-Runtime-Invoked-Function-Arn")

        val xrayTraceId = headers.get("Lambda-Runtime-Trace-Id")

        InvocationRequest(
          Id(requestId),
          remainingTimeInMillis,
          invokedFunctionArn,
          xrayTraceId,
          clientContext,
          cognitoIdentity,
          payload
        )
      }
}
