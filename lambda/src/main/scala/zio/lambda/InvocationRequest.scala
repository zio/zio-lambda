package zio.lambda

import zio.json._

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
  deadlineMs: Long,
  invokedFunctionArn: String,
  xrayTraceId: String,
  clientContext: Option[InvocationRequest.ClientContext],
  cognitoIdentity: Option[InvocationRequest.CognitoIdentity],
  payload: String
)

object InvocationRequest {

  final case class Id(value: String) extends AnyVal

  final case class ClientContext(client: ClientContext.Client, custom: Map[String, String], env: Map[String, String])

  object ClientContext {
    final case class Client(
      installationId: String,
      appTitle: String,
      appVersionName: String,
      appVersionCode: String,
      appPackageName: String
    )
    object Client {
      implicit val decoder: JsonDecoder[Client] = DeriveJsonDecoder.gen[Client]
    }
    implicit val decoder: JsonDecoder[ClientContext] = DeriveJsonDecoder.gen[ClientContext]
  }
  final case class CognitoIdentity(cognitoIdentityId: String, cognitoIdentityPoolId: String)
  object CognitoIdentity {
    implicit val decoder: JsonDecoder[CognitoIdentity] = DeriveJsonDecoder.gen[CognitoIdentity]
  }

  def fromHttpResponse(headers: Map[String, String], payload: String): Either[String, InvocationRequest] =
    for {
      requestId <- headers
                     .get("Lambda-Runtime-Aws-Request-Id")
                     .toRight("Lambda-Runtime-Aws-Request-Id is missing")
                     .map(Id(_))

      deadlineMs <- headers
                      .get("Lambda-Runtime-Deadline-Ms")
                      .toRight("Lambda-Runtime-Deadline-Ms is missing")
                      .flatMap(s => Try(s.toLong).toEither.left.map(_.getLocalizedMessage()))

      invokedFunctionArn <- headers
                              .get("Lambda-Runtime-Invoked-Function-Arn")
                              .toRight("Lambda-Runtime-Invoked-Function-Arn is missing")

      xrayTraceId <- headers
                       .get("Lambda-Runtime-Trace-Id")
                       .toRight("Lambda-Runtime-Trace-Id is missing")

    } yield {
      val clientContext = headers
        .get("Lambda-Runtime-Client-Context")
        .flatMap(_.fromJson[ClientContext].toOption)

      val cognitoIdentity = headers
        .get("Lambda-Runtime-Cognito-Identity")
        .flatMap(_.fromJson[CognitoIdentity].toOption)

      InvocationRequest(
        requestId,
        deadlineMs,
        invokedFunctionArn,
        xrayTraceId,
        clientContext,
        cognitoIdentity,
        payload
      )
    }
}
