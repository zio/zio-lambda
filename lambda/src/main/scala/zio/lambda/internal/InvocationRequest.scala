package zio.lambda.internal

import zio.lambda.ClientContext
import zio.lambda.CognitoIdentity
import zio.json._
import zio.json.internal.FastStringReader

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
  id: String,
  remainingTimeInMillis: Long,
  invokedFunctionArn: String,
  xrayTraceId: Option[String],
  clientContext: Option[ClientContext],
  cognitoIdentity: Option[CognitoIdentity],
  payload: String
)

object InvocationRequest {

  private[internal] def fromHttpResponse(
    headers: java.util.Map[String, java.util.List[String]],
    payload: String
  ): InvocationRequest =
    InvocationRequest(
      headers.get("Lambda-Runtime-Aws-Request-Id").get(0),
      headers.get("Lambda-Runtime-Deadline-Ms").get(0).toLong,
      headers.get("Lambda-Runtime-Invoked-Function-Arn").get(0),
      extractHeader("Lambda-Runtime-Trace-Id", headers),
      parseHeader[ClientContext]("Lambda-Runtime-Client-Context", headers),
      parseHeader[CognitoIdentity]("Lambda-Runtime-Cognito-Identity", headers),
      payload
    )

  private def extractHeader(
    header: String,
    headers: java.util.Map[String, java.util.List[String]]
  ): Option[String] = {
    val values = headers.get(header)
    if (values != null && !values.isEmpty()) {
      Some(values.get(0))
    } else None
  }

  private def parseHeader[A](
    header: String,
    headers: java.util.Map[String, java.util.List[String]]
  )(implicit decoder: JsonDecoder[A]): Option[A] =
    extractHeader(header, headers).map(value => decoder.unsafeDecode(Nil, new FastStringReader(value)))
}
