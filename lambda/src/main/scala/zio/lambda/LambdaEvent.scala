package zio.lambda

import zio.json._

sealed trait LambdaEvent

object LambdaEvent {

  final case class ActiveMQ(
    eventSource: String,
    eventSourceArn: String,
    messages: List[ActiveMQ.Message]
  ) extends LambdaEvent

  object ActiveMQ {

    implicit val decoder: JsonDecoder[ActiveMQ] = DeriveJsonDecoder.gen[ActiveMQ]

    final case class Message(
      messageID: String,
      messageType: String,
      timestamp: Long,
      deliveryMode: Int,
      correlationID: String,
      replyTo: String,
      destination: Destination,
      redelivered: Boolean,
      `type`: String,
      expiration: Long,
      priority: Int,
      data: String,
      brokerInTime: Long,
      brokerOutTime: Long
    )

    object Message {
      implicit val decoder: JsonDecoder[Message] = DeriveJsonDecoder.gen[Message]
    }

    final case class Destination(physicalName: String)

    object Destination {
      implicit val decoder: JsonDecoder[Destination] = DeriveJsonDecoder.gen[Destination]
    }
  }

  /**
   * The API Gateway customer authorizer event object as described
   * https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-lambda-authorizer.html
   */
  final case class APIGatewayCustomAuthorizer(
    version: String,
    `type`: String,
    methodArn: String,
    identitySource: String,
    authorizationToken: String,
    resource: String,
    path: String,
    httpMethod: String,
    headers: Map[String, String],
    queryStringParameters: Map[String, String],
    pathParameters: Map[String, String],
    stageVariables: Map[String, String],
    requestContext: APIGatewayCustomAuthorizer.RequestContext
  ) extends LambdaEvent

  object APIGatewayCustomAuthorizer {

    implicit val decoder: JsonDecoder[APIGatewayCustomAuthorizer] = DeriveJsonDecoder.gen[APIGatewayCustomAuthorizer]
    final case class RequestContext(
      path: String,
      accountId: String,
      resourceId: String,
      stage: String,
      requestId: String,
      identity: Identity,
      resourcePath: String,
      httpMethod: String,
      apiId: String
    )
    object RequestContext {
      implicit val decoder: JsonDecoder[RequestContext] = DeriveJsonDecoder.gen[RequestContext]
    }

    final case class Identity(apiKey: String, sourceIp: String)
    object Identity {
      implicit val decoder: JsonDecoder[Identity] = DeriveJsonDecoder.gen[Identity]
    }
  }

  /**
   * The API Gateway proxy request event as described
   * https://docs.aws.amazon.com/lambda/latest/dg/services-apigateway.html
   */
  final case class APIGatewayProxyRequest(
    resource: String,
    path: String,
    httpMethod: String,
    headers: Map[String, String],
    multiValueHeaders: Map[String, List[String]],
    queryStringParameters: Map[String, String],
    multiValueQueryStringParameters: Map[String, List[String]],
    pathParameters: Map[String, String],
    stageVariables: Map[String, String],
    requestContext: APIGatewayProxyRequest.RequestContext,
    body: String,
    isBase64Encoded: Boolean
  ) extends LambdaEvent

  object APIGatewayProxyRequest {
    implicit val decoder: JsonDecoder[APIGatewayProxyRequest] = DeriveJsonDecoder.gen[APIGatewayProxyRequest]

    final case class RequestContext(
      accountId: String,
      stage: String,
      resourceId: String,
      requestId: String,
      operationName: String,
      identity: Identity,
      resourcePath: String,
      httpMethod: String,
      apiId: String,
      path: String,
      // FIXME the value is a json object. Check all possible values
      authorizer: Map[String, String]
    )
    object RequestContext {
      implicit val decoder: JsonDecoder[RequestContext] = DeriveJsonDecoder.gen[RequestContext]
    }

    final case class Identity(
      cognitoIdentityPoolId: String,
      accountId: String,
      cognitoIdentityId: String,
      caller: String,
      apiKey: String,
      principalOrgId: String,
      sourceIp: String,
      cognitoAuthenticationType: String,
      cognitoAuthenticationProvider: String,
      userArn: String,
      userAgent: String,
      user: String,
      accessKey: String
    )
    object Identity {
      implicit val decoder: JsonDecoder[Identity] = DeriveJsonDecoder.gen[Identity]
    }
  }

  /**
   * The API Gateway customer authorizer event object as described
   * https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-lambda-authorizer.html
   */
  final case class APIGatewayV2CustomAuthorizer(
    version: String,
    `type`: String,
    routeArn: String,
    identitySource: List[String],
    routeKey: String,
    rawPath: String,
    rawQueryString: String,
    cookies: List[String],
    headers: Map[String, String],
    queryStringParameters: Map[String, String],
    requestContext: APIGatewayV2CustomAuthorizer.RequestContext,
    pathParameters: Map[String, String],
    stageVariables: Map[String, String]
  ) extends LambdaEvent

  object APIGatewayV2CustomAuthorizer {
    final case class RequestContext(
      accountId: String,
      apiId: String,
      domainName: String,
      domainPrefix: String,
      http: Http,
      requestId: String,
      routeKey: String,
      stage: String,
      time: java.time.Instant,
      timeEpoch: Long
    )
    object RequestContext {
      implicit val decoder: JsonDecoder[RequestContext] = DeriveJsonDecoder.gen[RequestContext]
    }

    final case class Http(
      method: String,
      path: String,
      protocol: String,
      sourceIp: String,
      userAgent: String
    )
    object Http {
      implicit val decoder: JsonDecoder[Http] = DeriveJsonDecoder.gen[Http]
    }
  }

  /**
   * API Gateway v2 event: https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-develop-integrations-lambda.html
   */
  final case class APIGatewayV2HTTP(
    version: String,
    routeKey: String,
    rawPath: String,
    rawQueryString: String,
    cookies: List[String],
    headers: Map[String, String],
    queryStringParameters: Map[String, String],
    pathParameters: Map[String, String],
    stageVariables: Map[String, String],
    body: String,
    isBase64Encoded: Boolean,
    requestContext: APIGatewayV2HTTP.RequestContext
  ) extends LambdaEvent

  object APIGatewayV2HTTP {
    implicit val decoder: JsonDecoder[APIGatewayV2HTTP] = DeriveJsonDecoder.gen[APIGatewayV2HTTP]
    final case class RequestContext(
      routeKey: String,
      accountId: String,
      stage: String,
      apiId: String,
      domainName: String,
      domainPrefix: String,
      time: String,
      timeEpoch: Long,
      http: Http,
      authorizer: Authorizer,
      requestId: String
    )
    object RequestContext {
      implicit val decoder: JsonDecoder[RequestContext] = DeriveJsonDecoder.gen[RequestContext]
    }

    final case class Http(
      method: String,
      path: String,
      protocol: String,
      sourceIp: String,
      userAgent: String
    )
    object Http {
      implicit val decoder: JsonDecoder[Http] = DeriveJsonDecoder.gen[Http]
    }

    final case class Authorizer(
      jwt: JWT,
      // FIXME the value is a json object. Check all possible values
      lambda: Map[String, String],
      iam: IAM
    )
    object Authorizer {
      implicit val decoder: JsonDecoder[Authorizer] = DeriveJsonDecoder.gen[Authorizer]
    }

    final case class JWT(claims: Map[String, String], scopes: List[String])
    object JWT {
      implicit val decoder: JsonDecoder[JWT] = DeriveJsonDecoder.gen[JWT]
    }
    final case class IAM(
      accessKey: String,
      accountId: String,
      callerId: String,
      cognitoIdentity: CognitoIdentity,
      principalOrgId: String,
      userArn: String,
      userId: String
    )
    object IAM {
      implicit val decoder: JsonDecoder[IAM] = DeriveJsonDecoder.gen[IAM]
    }

    final case class CognitoIdentity(amr: List[String], identityId: String, identityPoolId: String)
    object CognitoIdentity {
      implicit val decoder: JsonDecoder[CognitoIdentity] = DeriveJsonDecoder.gen[CognitoIdentity]
    }
  }

  final case class APIGatewayV2WebSocket(
    resource: String,
    path: String,
    httpMethod: String,
    headers: Map[String, String],
    multiValueHeaders: Map[String, List[String]],
    queryStringParameters: Map[String, String],
    multiValueQueryStringParameters: Map[String, List[String]],
    pathParameters: Map[String, String],
    stageVariables: Map[String, String],
    requestContext: APIGatewayV2WebSocket.RequestContext,
    body: String,
    isBase64Encoded: Boolean
  ) extends LambdaEvent

  object APIGatewayV2WebSocket {
    implicit val decoder: JsonDecoder[APIGatewayV2WebSocket] = DeriveJsonDecoder.gen[APIGatewayV2WebSocket]
    final case class RequestContext(
      accountId: String,
      resourceId: String,
      stage: String,
      requestId: String,
      identity: RequestIdentity,
      resourcePath: String,
      // FIXME the value is a json object. Check all possible values
      authorizer: Map[String, String],
      httpMethod: String,
      apiId: String,
      connectedAt: Long,
      connectionId: String,
      domainName: String,
      error: String,
      eventType: String,
      extendedRequestId: String,
      integrationLatency: String,
      messageDirection: String,
      messageId: String,
      requestTime: String,
      requestTimeEpoch: Long,
      routeKey: String,
      status: String
    )
    object RequestContext {
      implicit val decoder: JsonDecoder[RequestContext] = DeriveJsonDecoder.gen[RequestContext]
    }

    final case class RequestIdentity(
      cognitoIdentityPoolId: String,
      accountId: String,
      cognitoIdentityId: String,
      caller: String,
      apiKey: String,
      sourceIp: String,
      cognitoAuthenticationType: String,
      cognitoAuthenticationProvider: String,
      userArn: String,
      userAgent: String,
      user: String,
      accessKey: String
    )
    object RequestIdentity {
      implicit val decoder: JsonDecoder[RequestIdentity] = DeriveJsonDecoder.gen[RequestIdentity]
    }
  }

  final case class ApplicationLoadBalancerRequest(
    requestContext: ApplicationLoadBalancerRequest.RequestContext,
    httpMethod: String,
    path: String,
    queryStringParameters: Map[String, String],
    multiValueQueryStringParameters: Map[String, List[String]],
    headers: Map[String, String],
    multiValueHeaders: Map[String, List[String]],
    body: String,
    isBase64Encoded: Boolean
  ) extends LambdaEvent

  object ApplicationLoadBalancerRequest {
    implicit val decoder: JsonDecoder[ApplicationLoadBalancerRequest] =
      DeriveJsonDecoder.gen[ApplicationLoadBalancerRequest]
    final case class RequestContext(elb: Elb)
    object RequestContext {
      implicit val decoder: JsonDecoder[RequestContext] = DeriveJsonDecoder.gen[RequestContext]
    }

    final case class Elb(targetGroupArn: String)
    object Elb {
      implicit val decoder: JsonDecoder[Elb] = DeriveJsonDecoder.gen[Elb]
    }
  }

  final case class CloudFormationCustomResource(
    requestType: String,
    serviceToken: String,
    responseUrl: String,
    stackId: String,
    requestId: String,
    logicalResourceId: String,
    physicalResourceId: String,
    resourceType: String,
    // FIXME value's type shouldn't be Any
    resourceProperties: Map[String, String],
    oldResourceProperties: Map[String, String]
  ) extends LambdaEvent

  object CloudFormationCustomResource {
    implicit val decoder: JsonDecoder[CloudFormationCustomResource] =
      DeriveJsonDecoder.gen[CloudFormationCustomResource]
  }

  final case class CloudFront(
    records: List[CloudFront.Record]
  ) extends LambdaEvent

  object CloudFront {
    implicit val decoder: JsonDecoder[CloudFront] = DeriveJsonDecoder.gen[CloudFront]
    final case class Record(cf: CF)
    object Record {
      implicit val decoder: JsonDecoder[Record] = DeriveJsonDecoder.gen[Record]
    }

    final case class CF(
      config: Config,
      request: Request,
      response: Response
    )
    object CF {
      implicit val decoder: JsonDecoder[CF] = DeriveJsonDecoder.gen[CF]
    }

    final case class Config(distributionId: String)
    object Config {
      implicit val decoder: JsonDecoder[Config] = DeriveJsonDecoder.gen[Config]
    }
    final case class Request(
      uri: String,
      method: String,
      httpVersion: String,
      clientIp: String,
      headers: Map[String, List[Header]]
    )
    object Request {
      implicit val decoder: JsonDecoder[Request] = DeriveJsonDecoder.gen[Request]
    }

    final case class Response(
      status: String,
      statusDescription: String,
      httpVersion: String,
      headers: Map[String, List[Header]]
    )
    object Response {
      implicit val decoder: JsonDecoder[Response] = DeriveJsonDecoder.gen[Response]
    }

    final case class Header(
      key: String,
      value: String
    )
    object Header {
      implicit val decoder: JsonDecoder[Header] = DeriveJsonDecoder.gen[Header]
    }
  }

  final case class CloudWatchLogs(
    awsLogs: CloudWatchLogs.AWSLogs
  ) extends LambdaEvent

  object CloudWatchLogs {
    implicit val decoder: JsonDecoder[CloudWatchLogs] = DeriveJsonDecoder.gen[CloudWatchLogs]

    final case class AWSLogs(data: String)
    object AWSLogs {
      implicit val decoder: JsonDecoder[AWSLogs] = DeriveJsonDecoder.gen[AWSLogs]
    }
  }

  final case class CodeCommit(records: Seq[CodeCommit.Record]) extends LambdaEvent

  object CodeCommit {
    implicit val decoder: JsonDecoder[CodeCommit] = DeriveJsonDecoder.gen[CodeCommit]
    final case class Record(
      eventId: String,
      eventVersion: String,
      eventTime: java.time.Instant,
      eventTriggerName: String,
      eventPartNumber: Int,
      codeCommit: Commit,
      eventName: String,
      eventTriggerConfigId: String,
      eventSourceArn: String,
      userIdentityArn: String,
      eventSource: String,
      awsRegion: String,
      customData: String,
      eventTotalParts: Int
    )
    object Record {
      implicit val decoder: JsonDecoder[Record] = DeriveJsonDecoder.gen[Record]
    }

    final case class Commit(references: List[Reference])
    object Commit {
      implicit val decoder: JsonDecoder[Commit] = DeriveJsonDecoder.gen[Commit]
    }

    final case class Reference(
      commit: String,
      ref: String,
      created: Boolean
    )
    object Reference {
      implicit val decoder: JsonDecoder[Reference] = DeriveJsonDecoder.gen[Reference]
    }
  }

  final case class Cognito(
    region: String,
    datasetRecords: Map[String, Cognito.DatasetRecord],
    identityPoolId: String,
    identityId: String,
    datasetName: String,
    eventType: String,
    version: Int
  ) extends LambdaEvent

  object Cognito {
    implicit val decoder: JsonDecoder[Cognito] = DeriveJsonDecoder.gen[Cognito]
    final case class DatasetRecord(
      oldValue: String,
      newValue: String,
      op: String
    )
    object DatasetRecord {
      implicit val decoder: JsonDecoder[DatasetRecord] = DeriveJsonDecoder.gen[DatasetRecord]
    }
  }

  final case class CognitoUserPoolCreateAuthChallenge(
    version: String,
    triggerSource: String,
    region: String,
    userPoolId: String,
    userName: String,
    callerContext: CognitoUserPoolCreateAuthChallenge.CallerContext,
    request: CognitoUserPoolCreateAuthChallenge.Request,
    response: CognitoUserPoolCreateAuthChallenge.Response
  ) extends LambdaEvent

  object CognitoUserPoolCreateAuthChallenge {
    implicit val decoder: JsonDecoder[CognitoUserPoolCreateAuthChallenge] =
      DeriveJsonDecoder.gen[CognitoUserPoolCreateAuthChallenge]

    final case class CallerContext(awsSdkVersion: String, clientId: String)
    object CallerContext {
      implicit val decoder: JsonDecoder[CallerContext] = DeriveJsonDecoder.gen[CallerContext]
    }

    final case class Request(
      clientMetadata: Map[String, String],
      challengeName: String,
      // session: List[ChallengeResult],
      userNotFound: Boolean
    )
    object Request {
      implicit val decoder: JsonDecoder[Request] = DeriveJsonDecoder.gen[Request]
    }

    final case class Response(
      publicChallengeParameters: Map[String, String],
      privateChallengeParameters: Map[String, String],
      challengeMetadata: String
    )
    object Response {
      implicit val decoder: JsonDecoder[Response] = DeriveJsonDecoder.gen[Response]
    }
  }

  final case class CognitoUserPoolCustomMessage(
    version: String,
    triggerSource: String,
    region: String,
    userPoolId: String,
    userName: String,
    callerContext: CognitoUserPoolCustomMessage.CallerContext,
    request: CognitoUserPoolCustomMessage.Request,
    response: CognitoUserPoolCustomMessage.Response
  ) extends LambdaEvent

  object CognitoUserPoolCustomMessage {
    implicit val decoder: JsonDecoder[CognitoUserPoolCustomMessage] =
      DeriveJsonDecoder.gen[CognitoUserPoolCustomMessage]

    final case class CallerContext(awsSdkVersion: String, clientId: String)
    object CallerContext {
      implicit val decoder: JsonDecoder[CallerContext] = DeriveJsonDecoder.gen[CallerContext]
    }

    final case class Request()
    object Request {
      implicit val decoder: JsonDecoder[Request] = DeriveJsonDecoder.gen[Request]
    }

    final case class Response()
    object Response {
      implicit val decoder: JsonDecoder[Response] = DeriveJsonDecoder.gen[Response]
    }

  }

  final case class CognitoUserPoolDefineAuthChallenge() extends LambdaEvent
  object CognitoUserPoolDefineAuthChallenge {
    implicit val decoder: JsonDecoder[CognitoUserPoolDefineAuthChallenge] =
      DeriveJsonDecoder.gen[CognitoUserPoolDefineAuthChallenge]
  }

  final case class CognitoUserPool() extends LambdaEvent
  object CognitoUserPool {
    implicit val decoder: JsonDecoder[CognitoUserPool] = DeriveJsonDecoder.gen[CognitoUserPool]
  }

  final case class CognitoUserPoolMigrateUser() extends LambdaEvent
  object CognitoUserPoolMigrateUser {
    implicit val decoder: JsonDecoder[CognitoUserPoolMigrateUser] = DeriveJsonDecoder.gen[CognitoUserPoolMigrateUser]
  }

  final case class CognitoUserPoolPostAuthentication() extends LambdaEvent
  object CognitoUserPoolPostAuthentication {
    implicit val decoder: JsonDecoder[CognitoUserPoolPostAuthentication] =
      DeriveJsonDecoder.gen[CognitoUserPoolPostAuthentication]
  }

  final case class CognitoUserPoolPostConfirmation() extends LambdaEvent
  object CognitoUserPoolPostConfirmation {
    implicit val decoder: JsonDecoder[CognitoUserPoolPostConfirmation] =
      DeriveJsonDecoder.gen[CognitoUserPoolPostConfirmation]
  }

  final case class CognitoUserPoolPreAuthentication() extends LambdaEvent
  object CognitoUserPoolPreAuthentication {
    implicit val decoder: JsonDecoder[CognitoUserPoolPreAuthentication] =
      DeriveJsonDecoder.gen[CognitoUserPoolPreAuthentication]
  }

  final case class CognitoUserPoolPreSignUp() extends LambdaEvent
  object CognitoUserPoolPreSignUp {
    implicit val decoder: JsonDecoder[CognitoUserPoolPreSignUp] = DeriveJsonDecoder.gen[CognitoUserPoolPreSignUp]
  }

  final case class CognitoUserPoolPreTokenGeneration() extends LambdaEvent
  object CognitoUserPoolPreTokenGeneration {
    implicit val decoder: JsonDecoder[CognitoUserPoolPreTokenGeneration] =
      DeriveJsonDecoder.gen[CognitoUserPoolPreTokenGeneration]
  }

  final case class CognitoUserPoolVerifyAuthChallengeResponse() extends LambdaEvent
  object CognitoUserPoolVerifyAuthChallengeResponse {
    implicit val decoder: JsonDecoder[CognitoUserPoolVerifyAuthChallengeResponse] =
      DeriveJsonDecoder.gen[CognitoUserPoolVerifyAuthChallengeResponse]
  }

  final case class Config(
    version: String,
    invokingEvent: String,
    ruleParameters: String,
    resultToken: String,
    configRuleArn: String,
    configRuleId: String,
    configRuleName: String,
    accountId: String,
    executionRoleArn: String,
    eventLeftScope: Boolean
  ) extends LambdaEvent

  object Config {
    implicit val decoder: JsonDecoder[Config] = DeriveJsonDecoder.gen[Config]
  }

  final case class Connect(details: Connect.Details, name: String) extends LambdaEvent

  object Connect {
    implicit val decoder: JsonDecoder[Connect] = DeriveJsonDecoder.gen[Connect]

    final case class Details(contactData: ContactData, parameters: Map[String, String])
    object Details {
      implicit val decoder: JsonDecoder[Details] = DeriveJsonDecoder.gen[Details]
    }
    final case class ContactData(
      attributes: Map[String, String],
      channel: String,
      contactId: String,
      customerEndpoint: Endpoint,
      initialContactId: String,
      initiationMethod: String,
      instanceArn: String,
      previousContactId: String,
      queue: String,
      systemEndpoint: Endpoint
    )
    object ContactData {
      implicit val decoder: JsonDecoder[ContactData] = DeriveJsonDecoder.gen[ContactData]
    }
    final case class Endpoint(address: String, `type`: String)
    object Endpoint {
      implicit val decoder: JsonDecoder[Endpoint] = DeriveJsonDecoder.gen[Endpoint]
    }
  }

  // final case class Dynamodb(records: List[Dynamodb.DynamodbStreamRecord]) extends LambdaEvent

  // object Dynamodb {
  //   implicit val codec: JsonValueCodec[Dynamodb] = JsonCodecMaker.make
  //   final case class DynamodbStreamRecord(
  //     eventName: String,
  //     eventVersion: String,
  //     eventSource: String,
  //     awsRegion: String,
  //     eventSourceARN: String,
  //     dynamodb: StreamRecord,
  //     userIdentity: Identity
  //   )
  //   object DynamodbStreamRecord {
  //     implicit val codec: JsonValueCodec[DynamodbStreamRecord] = JsonCodecMaker.make
  //   }
  //   final case class StreamRecord(
  //     approximateCreationDateTime: java.time.Instant,
  //     keys: Map[String, AttributeValue],
  //     newImage: Map[String, AttributeValue],
  //     oldImage: Map[String, AttributeValue],
  //     sequenceNumber: String,
  //     sizeBytes: Long,
  //     streamViewType: String
  //   )
  //   object StreamRecord {
  //     implicit val codec: JsonValueCodec[StreamRecord] = JsonCodecMaker.make
  //   }

  //   final case class Identity(principalId: String, `type`: String)
  //   object Identity {
  //     implicit val codec: JsonValueCodec[Identity] = JsonCodecMaker.make
  //   }
  //   final case class AttributeValue(
  //     n: String,
  //     b: String, // Revisit this as it was defined as java.nio.ByteBuffer,
  //     sS: List[String],
  //     nS: List[String],
  //     bS: List[String], // Revisit this as it was defined as List[java.nio.ByteBuffer]
  //     m: Map[String, AttributeValue],
  //     l: List[AttributeValue],
  //     nULLValue: Boolean,
  //     bOOL: Boolean
  //   )
  //   object AttributeValue {
  //     implicit val codec: JsonValueCodec[AttributeValue] = JsonCodecMaker.make
  //   }
  // }

  // final case class DynamodbTimeWindow(records: Dynamodb.DynamodbStreamRecord)
  // object DynamodbTimeWindow {
  //   implicit val codec: JsonValueCodec[DynamodbTimeWindow] = JsonCodecMaker.make
  // }

  final case class IoTButton(
    serialNumber: String,
    clickType: String,
    batteryVoltage: String
  ) extends LambdaEvent
  object IoTButton {
    implicit val decoder: JsonDecoder[IoTButton] = DeriveJsonDecoder.gen[IoTButton]
  }

  final case class Kafka(
    records: Map[String, List[Kafka.Record]],
    eventSource: String,
    eventSourceArn: String,
    bootstrapServers: String
  ) extends LambdaEvent

  object Kafka {
    implicit val decoder: JsonDecoder[Kafka] = DeriveJsonDecoder.gen[Kafka]
    final case class Record(
      topic: String,
      partition: Int,
      offset: Long,
      timestamp: Long,
      timestampType: String,
      key: String,
      value: String
    )
    object Record {
      implicit val decoder: JsonDecoder[Record] = DeriveJsonDecoder.gen[Record]
    }
  }

  final case class KinesisAnalyticsFirehoseInputPreprocessing(
    invocationId: String,
    applicationArn: String,
    streamArn: String,
    records: List[KinesisAnalyticsFirehoseInputPreprocessing.Record]
  ) extends LambdaEvent
  object KinesisAnalyticsFirehoseInputPreprocessing {
    implicit val decoder: JsonDecoder[KinesisAnalyticsFirehoseInputPreprocessing] =
      DeriveJsonDecoder.gen[KinesisAnalyticsFirehoseInputPreprocessing]
    final case class Record(
      recordId: String,
      kinesisFirehoseRecordMetadata: KinesisFirehoseRecordMetadata,
      data: String // Revisit this as it was defined as java.nio.ByteBuffer
    )
    object Record {
      implicit val decoder: JsonDecoder[Record] = DeriveJsonDecoder.gen[Record]
    }
    final case class KinesisFirehoseRecordMetadata(approximateArrivalTimestamp: Long)
    object KinesisFirehoseRecordMetadata {
      implicit val decoder: JsonDecoder[KinesisFirehoseRecordMetadata] =
        DeriveJsonDecoder.gen[KinesisFirehoseRecordMetadata]
    }
  }

  final case class KinesisAnalyticsOutputDelivery(
    invocationId: String,
    applicationArn: String,
    records: List[KinesisAnalyticsOutputDelivery.Record]
  ) extends LambdaEvent

  object KinesisAnalyticsOutputDelivery {
    implicit val decoder: JsonDecoder[KinesisAnalyticsOutputDelivery] =
      DeriveJsonDecoder.gen[KinesisAnalyticsOutputDelivery]
    final case class Record(
      recordId: String,
      lambdaDeliveryRecordMetadata: LambdaDeliveryRecordMetadata,
      data: String // Revisit this as it was defined as java.nio.ByteBuffer
    )
    object Record {
      implicit val decoder: JsonDecoder[Record] = DeriveJsonDecoder.gen[Record]
    }
    final case class LambdaDeliveryRecordMetadata(retryHint: Long)
    object LambdaDeliveryRecordMetadata {
      implicit val decoder: JsonDecoder[LambdaDeliveryRecordMetadata] =
        DeriveJsonDecoder.gen[LambdaDeliveryRecordMetadata]
    }
  }

  final case class KinesisAnalyticsStreamsInputPreprocessing() extends LambdaEvent
  object KinesisAnalyticsStreamsInputPreprocessing {
    implicit val decoder: JsonDecoder[KinesisAnalyticsStreamsInputPreprocessing] =
      DeriveJsonDecoder.gen[KinesisAnalyticsStreamsInputPreprocessing]
  }

  final case class Kinesis() extends LambdaEvent
  object Kinesis {
    implicit val decoder: JsonDecoder[Kinesis] = DeriveJsonDecoder.gen[Kinesis]
  }

  final case class KinesisFirehose() extends LambdaEvent
  object KinesisFirehose {
    implicit val decoder: JsonDecoder[KinesisFirehose] = DeriveJsonDecoder.gen[KinesisFirehose]
  }

  final case class LambdaDestination() extends LambdaEvent {
    implicit val decoder: JsonDecoder[LambdaDestination] = DeriveJsonDecoder.gen[LambdaDestination]
  }

  final case class Lex() extends LambdaEvent {
    implicit val decoder: JsonDecoder[Lex] = DeriveJsonDecoder.gen[Lex]
  }

  final case class S3Batch() extends LambdaEvent
  object S3Batch {
    implicit val decoder: JsonDecoder[S3Batch] = DeriveJsonDecoder.gen[S3Batch]
  }

  final case class S3() extends LambdaEvent
  object S3 {
    implicit val decoder: JsonDecoder[S3] = DeriveJsonDecoder.gen[S3]
  }

  final case class Scheduled() extends LambdaEvent
  object Scheduled {
    implicit val decoder: JsonDecoder[Scheduled] = DeriveJsonDecoder.gen[Scheduled]
  }

  final case class SecretsManagerRotation() extends LambdaEvent
  object SecretsManagerRotation {
    implicit val decoder: JsonDecoder[SecretsManagerRotation] = DeriveJsonDecoder.gen[SecretsManagerRotation]
  }

  final case class SNS() extends LambdaEvent
  object SNS {
    implicit val decoder: JsonDecoder[SNS] = DeriveJsonDecoder.gen[SNS]
  }

  final case class SQS() extends LambdaEvent
  object SQS {
    implicit val decoder: JsonDecoder[SQS] = DeriveJsonDecoder.gen[SQS]
  }

}
