package zio.lambda

import zio.json._

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

sealed trait LambdaEvent

object LambdaEvent {

  final case class ActiveMQ(
    eventSource: String,
    eventSourceArn: String,
    messages: List[ActiveMQMessage]
  ) extends LambdaEvent

  object ActiveMQ {
    implicit val decoder: JsonDecoder[ActiveMQ] = DeriveJsonDecoder.gen[ActiveMQ]
  }

  final case class ActiveMQMessage(
    messageID: String,
    messageType: String,
    timestamp: Long,
    deliveryMode: Int,
    correlationID: String,
    replyTo: String,
    destination: ActiveMQMessageDestination,
    redelivered: Boolean,
    `type`: String,
    expiration: Long,
    priority: Int,
    data: String,
    brokerInTime: Long,
    brokerOutTime: Long
  )

  object ActiveMQMessage {
    implicit val decoder: JsonDecoder[ActiveMQMessage] = DeriveJsonDecoder.gen[ActiveMQMessage]
  }

  final case class ActiveMQMessageDestination(physicalName: String)

  object ActiveMQMessageDestination {
    implicit val decoder: JsonDecoder[ActiveMQMessageDestination] = DeriveJsonDecoder.gen[ActiveMQMessageDestination]
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
    requestContext: APIGatewayCustomAuthorizerRequestContext
  ) extends LambdaEvent

  object APIGatewayCustomAuthorizer {
    implicit val decoder: JsonDecoder[APIGatewayCustomAuthorizer] = DeriveJsonDecoder.gen[APIGatewayCustomAuthorizer]
  }

  final case class APIGatewayCustomAuthorizerRequestContext(
    path: String,
    accountId: String,
    resourceId: String,
    stage: String,
    requestId: String,
    identity: APIGatewayCustomAuthorizerRequestContextIdentity,
    resourcePath: String,
    httpMethod: String,
    apiId: String
  )
  object APIGatewayCustomAuthorizerRequestContext {
    implicit val decoder: JsonDecoder[APIGatewayCustomAuthorizerRequestContext] =
      DeriveJsonDecoder.gen[APIGatewayCustomAuthorizerRequestContext]
  }

  final case class APIGatewayCustomAuthorizerRequestContextIdentity(apiKey: String, sourceIp: String)
  object APIGatewayCustomAuthorizerRequestContextIdentity {
    implicit val decoder: JsonDecoder[APIGatewayCustomAuthorizerRequestContextIdentity] =
      DeriveJsonDecoder.gen[APIGatewayCustomAuthorizerRequestContextIdentity]
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
    requestContext: APIGatewayProxyRequestRequestContext,
    body: String,
    isBase64Encoded: Boolean
  ) extends LambdaEvent

  object APIGatewayProxyRequest {
    implicit val decoder: JsonDecoder[APIGatewayProxyRequest] = DeriveJsonDecoder.gen[APIGatewayProxyRequest]
  }

  final case class APIGatewayProxyRequestRequestContext(
    accountId: String,
    stage: String,
    resourceId: String,
    requestId: String,
    operationName: String,
    identity: APIGatewayProxyRequestRequestContextIdentity,
    resourcePath: String,
    httpMethod: String,
    apiId: String,
    path: String,
    // FIXME the value is a json object. Check all possible values
    authorizer: Map[String, String]
  )
  object APIGatewayProxyRequestRequestContext {
    implicit val decoder: JsonDecoder[APIGatewayProxyRequestRequestContext] =
      DeriveJsonDecoder.gen[APIGatewayProxyRequestRequestContext]
  }

  final case class APIGatewayProxyRequestRequestContextIdentity(
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
  object APIGatewayProxyRequestRequestContextIdentity {
    implicit val decoder: JsonDecoder[APIGatewayProxyRequestRequestContextIdentity] =
      DeriveJsonDecoder.gen[APIGatewayProxyRequestRequestContextIdentity]
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
    requestContext: APIGatewayV2CustomAuthorizerRequestContext,
    pathParameters: Map[String, String],
    stageVariables: Map[String, String]
  ) extends LambdaEvent

  object APIGatewayV2CustomAuthorizer {
    implicit val decoder: JsonDecoder[APIGatewayV2CustomAuthorizer] =
      DeriveJsonDecoder.gen[APIGatewayV2CustomAuthorizer]
  }

  final case class APIGatewayV2CustomAuthorizerRequestContext(
    accountId: String,
    apiId: String,
    domainName: String,
    domainPrefix: String,
    http: Http,
    requestId: String,
    routeKey: String,
    stage: String,
    time: java.time.OffsetDateTime,
    timeEpoch: Long
  )
  object APIGatewayV2CustomAuthorizerRequestContext {
    lazy val timeDateTimeFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z")

    implicit val timeDecoder: JsonDecoder[OffsetDateTime] =
      JsonDecoder[String].map(OffsetDateTime.parse(_, timeDateTimeFormatter))

    implicit val decoder: JsonDecoder[APIGatewayV2CustomAuthorizerRequestContext] =
      DeriveJsonDecoder.gen[APIGatewayV2CustomAuthorizerRequestContext]
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
    requestContext: APIGatewayV2HTTPRequestContext
  ) extends LambdaEvent

  object APIGatewayV2HTTP {
    implicit val decoder: JsonDecoder[APIGatewayV2HTTP] = DeriveJsonDecoder.gen[APIGatewayV2HTTP]
  }

  final case class APIGatewayV2HTTPRequestContext(
    routeKey: String,
    accountId: String,
    stage: String,
    apiId: String,
    domainName: String,
    domainPrefix: String,
    time: String,
    timeEpoch: Long,
    http: APIGatewayV2HTTPRequestContextHttp,
    authorizer: APIGatewayV2HTTPRequestContextAuthorizer,
    requestId: String
  )
  object APIGatewayV2HTTPRequestContext {
    implicit val decoder: JsonDecoder[APIGatewayV2HTTPRequestContext] =
      DeriveJsonDecoder.gen[APIGatewayV2HTTPRequestContext]
  }

  final case class APIGatewayV2HTTPRequestContextHttp(
    method: String,
    path: String,
    protocol: String,
    sourceIp: String,
    userAgent: String
  )
  object APIGatewayV2HTTPRequestContextHttp {
    implicit val decoder: JsonDecoder[APIGatewayV2HTTPRequestContextHttp] =
      DeriveJsonDecoder.gen[APIGatewayV2HTTPRequestContextHttp]
  }

  final case class APIGatewayV2HTTPRequestContextAuthorizer(
    jwt: APIGatewayV2HTTPRequestContextAuthorizerJWT,
    lambda: Map[String, String],
    iam: APIGatewayV2HTTPRequestContextAuthorizerIAM
  )
  object APIGatewayV2HTTPRequestContextAuthorizer {
    implicit val decoder: JsonDecoder[APIGatewayV2HTTPRequestContextAuthorizer] =
      DeriveJsonDecoder.gen[APIGatewayV2HTTPRequestContextAuthorizer]
  }

  final case class APIGatewayV2HTTPRequestContextAuthorizerJWT(claims: Map[String, String], scopes: List[String])
  object APIGatewayV2HTTPRequestContextAuthorizerJWT {
    implicit val decoder: JsonDecoder[APIGatewayV2HTTPRequestContextAuthorizerJWT] =
      DeriveJsonDecoder.gen[APIGatewayV2HTTPRequestContextAuthorizerJWT]
  }
  final case class APIGatewayV2HTTPRequestContextAuthorizerIAM(
    accessKey: String,
    accountId: String,
    callerId: String,
    cognitoIdentity: APIGatewayV2HTTPRequestContextAuthorizerIAMCognitoIdentity,
    principalOrgId: String,
    userArn: String,
    userId: String
  )
  object APIGatewayV2HTTPRequestContextAuthorizerIAM {
    implicit val decoder: JsonDecoder[APIGatewayV2HTTPRequestContextAuthorizerIAM] =
      DeriveJsonDecoder.gen[APIGatewayV2HTTPRequestContextAuthorizerIAM]
  }

  final case class APIGatewayV2HTTPRequestContextAuthorizerIAMCognitoIdentity(
    amr: List[String],
    identityId: String,
    identityPoolId: String
  )
  object APIGatewayV2HTTPRequestContextAuthorizerIAMCognitoIdentity {
    implicit val decoder: JsonDecoder[APIGatewayV2HTTPRequestContextAuthorizerIAMCognitoIdentity] =
      DeriveJsonDecoder.gen[APIGatewayV2HTTPRequestContextAuthorizerIAMCognitoIdentity]
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
    requestContext: APIGatewayV2WebSocketRequestContext,
    body: String,
    isBase64Encoded: Boolean
  ) extends LambdaEvent

  object APIGatewayV2WebSocket {
    implicit val decoder: JsonDecoder[APIGatewayV2WebSocket] = DeriveJsonDecoder.gen[APIGatewayV2WebSocket]
  }

  final case class APIGatewayV2WebSocketRequestContext(
    accountId: String,
    resourceId: String,
    stage: String,
    requestId: String,
    identity: APIGatewayV2WebSocketRequestContextIdentity,
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
  object APIGatewayV2WebSocketRequestContext {
    implicit val decoder: JsonDecoder[APIGatewayV2WebSocketRequestContext] =
      DeriveJsonDecoder.gen[APIGatewayV2WebSocketRequestContext]
  }

  final case class APIGatewayV2WebSocketRequestContextIdentity(
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
  object APIGatewayV2WebSocketRequestContextIdentity {
    implicit val decoder: JsonDecoder[APIGatewayV2WebSocketRequestContextIdentity] =
      DeriveJsonDecoder.gen[APIGatewayV2WebSocketRequestContextIdentity]
  }

  final case class ApplicationLoadBalancerRequest(
    requestContext: ApplicationLoadBalancerRequestContext,
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
  }

  final case class ApplicationLoadBalancerRequestContext(elb: ApplicationLoadBalancerRequestContextElb)
  object ApplicationLoadBalancerRequestContext {
    implicit val decoder: JsonDecoder[ApplicationLoadBalancerRequestContext] =
      DeriveJsonDecoder.gen[ApplicationLoadBalancerRequestContext]
  }

  final case class ApplicationLoadBalancerRequestContextElb(targetGroupArn: String)
  object ApplicationLoadBalancerRequestContextElb {
    implicit val decoder: JsonDecoder[ApplicationLoadBalancerRequestContextElb] =
      DeriveJsonDecoder.gen[ApplicationLoadBalancerRequestContextElb]
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
    records: List[CloudFrontRecord]
  ) extends LambdaEvent

  object CloudFront {
    implicit val decoder: JsonDecoder[CloudFront] = DeriveJsonDecoder.gen[CloudFront]

  }

  final case class CloudFrontRecord(cf: CloudFrontRecordCF)
  object CloudFrontRecord {
    implicit val decoder: JsonDecoder[CloudFrontRecord] = DeriveJsonDecoder.gen[CloudFrontRecord]
  }

  final case class CloudFrontRecordCF(
    config: CloudFrontRecordCFConfig,
    request: CloudFrontRecordCFRequest,
    response: CloudFrontRecordCFResponse
  )
  object CloudFrontRecordCF {
    implicit val decoder: JsonDecoder[CloudFrontRecordCF] = DeriveJsonDecoder.gen[CloudFrontRecordCF]
  }

  final case class CloudFrontRecordCFConfig(distributionId: String)
  object CloudFrontRecordCFConfig {
    implicit val decoder: JsonDecoder[CloudFrontRecordCFConfig] = DeriveJsonDecoder.gen[CloudFrontRecordCFConfig]
  }
  final case class CloudFrontRecordCFRequest(
    uri: String,
    method: String,
    httpVersion: String,
    clientIp: String,
    headers: Map[String, List[CloudFrontRecordCFHttpHeader]]
  )
  object CloudFrontRecordCFRequest {
    implicit val decoder: JsonDecoder[CloudFrontRecordCFRequest] = DeriveJsonDecoder.gen[CloudFrontRecordCFRequest]
  }

  final case class CloudFrontRecordCFResponse(
    status: String,
    statusDescription: String,
    httpVersion: String,
    headers: Map[String, List[CloudFrontRecordCFHttpHeader]]
  )
  object CloudFrontRecordCFResponse {
    implicit val decoder: JsonDecoder[CloudFrontRecordCFResponse] = DeriveJsonDecoder.gen[CloudFrontRecordCFResponse]
  }

  final case class CloudFrontRecordCFHttpHeader(
    key: String,
    value: String
  )
  object CloudFrontRecordCFHttpHeader {
    implicit val decoder: JsonDecoder[CloudFrontRecordCFHttpHeader] =
      DeriveJsonDecoder.gen[CloudFrontRecordCFHttpHeader]
  }

  final case class CloudWatchLogs(
    awsLogs: CloudWatchLogsAWSLogs
  ) extends LambdaEvent

  object CloudWatchLogs {
    implicit val decoder: JsonDecoder[CloudWatchLogs] = DeriveJsonDecoder.gen[CloudWatchLogs]
  }

  final case class CloudWatchLogsAWSLogs(data: String)
  object CloudWatchLogsAWSLogs {
    implicit val decoder: JsonDecoder[CloudWatchLogsAWSLogs] = DeriveJsonDecoder.gen[CloudWatchLogsAWSLogs]
  }

  final case class CodeCommit(records: Seq[CodeCommitRecord]) extends LambdaEvent

  object CodeCommit {
    implicit val decoder: JsonDecoder[CodeCommit] = DeriveJsonDecoder.gen[CodeCommit]
  }

  final case class CodeCommitRecord(
    eventId: String,
    eventVersion: String,
    eventTime: java.time.Instant,
    eventTriggerName: String,
    eventPartNumber: Int,
    codeCommit: CodeCommitRecordCommit,
    eventName: String,
    eventTriggerConfigId: String,
    eventSourceArn: String,
    userIdentityArn: String,
    eventSource: String,
    awsRegion: String,
    customData: String,
    eventTotalParts: Int
  )
  object CodeCommitRecord {
    implicit val decoder: JsonDecoder[CodeCommitRecord] = DeriveJsonDecoder.gen[CodeCommitRecord]
  }

  final case class CodeCommitRecordCommit(references: List[CodeCommitRecordCommitReference])
  object CodeCommitRecordCommit {
    implicit val decoder: JsonDecoder[CodeCommitRecordCommit] = DeriveJsonDecoder.gen[CodeCommitRecordCommit]
  }

  final case class CodeCommitRecordCommitReference(
    commit: String,
    ref: String,
    created: Boolean
  )
  object CodeCommitRecordCommitReference {
    implicit val decoder: JsonDecoder[CodeCommitRecordCommitReference] =
      DeriveJsonDecoder.gen[CodeCommitRecordCommitReference]
  }

  final case class Cognito(
    region: String,
    datasetRecords: Map[String, CognitoDatasetRecord],
    identityPoolId: String,
    identityId: String,
    datasetName: String,
    eventType: String,
    version: Int
  ) extends LambdaEvent

  object Cognito {
    implicit val decoder: JsonDecoder[Cognito] = DeriveJsonDecoder.gen[Cognito]
  }

  final case class CognitoDatasetRecord(
    oldValue: String,
    newValue: String,
    op: String
  )
  object CognitoDatasetRecord {
    implicit val decoder: JsonDecoder[CognitoDatasetRecord] = DeriveJsonDecoder.gen[CognitoDatasetRecord]
  }

  final case class CognitoUserPoolCreateAuthChallenge(
    version: String,
    triggerSource: String,
    region: String,
    userPoolId: String,
    userName: String,
    callerContext: CognitoUserPoolCreateAuthChallengeCallerContext,
    request: CognitoUserPoolCreateAuthChallengeRequest,
    response: CognitoUserPoolCreateAuthChallengeResponse
  ) extends LambdaEvent

  object CognitoUserPoolCreateAuthChallenge {
    implicit val decoder: JsonDecoder[CognitoUserPoolCreateAuthChallenge] =
      DeriveJsonDecoder.gen[CognitoUserPoolCreateAuthChallenge]
  }

  final case class CognitoUserPoolCreateAuthChallengeCallerContext(awsSdkVersion: String, clientId: String)
  object CognitoUserPoolCreateAuthChallengeCallerContext {
    implicit val decoder: JsonDecoder[CognitoUserPoolCreateAuthChallengeCallerContext] =
      DeriveJsonDecoder.gen[CognitoUserPoolCreateAuthChallengeCallerContext]
  }

  final case class CognitoUserPoolCreateAuthChallengeRequest(
    clientMetadata: Map[String, String],
    challengeName: String,
    // session: List[ChallengeResult],
    userNotFound: Boolean
  )
  object CognitoUserPoolCreateAuthChallengeRequest {
    implicit val decoder: JsonDecoder[CognitoUserPoolCreateAuthChallengeRequest] =
      DeriveJsonDecoder.gen[CognitoUserPoolCreateAuthChallengeRequest]
  }

  final case class CognitoUserPoolCreateAuthChallengeResponse(
    publicChallengeParameters: Map[String, String],
    privateChallengeParameters: Map[String, String],
    challengeMetadata: String
  )
  object CognitoUserPoolCreateAuthChallengeResponse {
    implicit val decoder: JsonDecoder[CognitoUserPoolCreateAuthChallengeResponse] =
      DeriveJsonDecoder.gen[CognitoUserPoolCreateAuthChallengeResponse]
  }

  final case class CognitoUserPoolCustomMessage(
    version: String,
    triggerSource: String,
    region: String,
    userPoolId: String,
    userName: String,
    callerContext: CognitoUserPoolCustomMessageCallerContext,
    request: CognitoUserPoolCustomMessageRequest,
    response: CognitoUserPoolCustomMessageResponse
  ) extends LambdaEvent

  object CognitoUserPoolCustomMessage {
    implicit val decoder: JsonDecoder[CognitoUserPoolCustomMessage] =
      DeriveJsonDecoder.gen[CognitoUserPoolCustomMessage]
  }

  final case class CognitoUserPoolCustomMessageCallerContext(awsSdkVersion: String, clientId: String)
  object CognitoUserPoolCustomMessageCallerContext {
    implicit val decoder: JsonDecoder[CognitoUserPoolCustomMessageCallerContext] =
      DeriveJsonDecoder.gen[CognitoUserPoolCustomMessageCallerContext]
  }

  final case class CognitoUserPoolCustomMessageRequest()
  object CognitoUserPoolCustomMessageRequest {
    implicit val decoder: JsonDecoder[CognitoUserPoolCustomMessageRequest] =
      DeriveJsonDecoder.gen[CognitoUserPoolCustomMessageRequest]
  }

  final case class CognitoUserPoolCustomMessageResponse()
  object CognitoUserPoolCustomMessageResponse {
    implicit val decoder: JsonDecoder[CognitoUserPoolCustomMessageResponse] =
      DeriveJsonDecoder.gen[CognitoUserPoolCustomMessageResponse]
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

  final case class Connect(details: ConnectDetails, name: String) extends LambdaEvent

  object Connect {
    implicit val decoder: JsonDecoder[Connect] = DeriveJsonDecoder.gen[Connect]
  }

  final case class ConnectDetails(contactData: ConnectContactData, parameters: Map[String, String])
  object ConnectDetails {
    implicit val decoder: JsonDecoder[ConnectDetails] = DeriveJsonDecoder.gen[ConnectDetails]
  }

  final case class ConnectContactData(
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
  object ConnectContactData {
    implicit val decoder: JsonDecoder[ConnectContactData] = DeriveJsonDecoder.gen[ConnectContactData]
  }
  final case class Endpoint(address: String, `type`: String)
  object Endpoint {
    implicit val decoder: JsonDecoder[Endpoint] = DeriveJsonDecoder.gen[Endpoint]
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
    records: Map[String, List[KafkaRecord]],
    eventSource: String,
    eventSourceArn: String,
    bootstrapServers: String
  ) extends LambdaEvent

  object Kafka {
    implicit val decoder: JsonDecoder[Kafka] = DeriveJsonDecoder.gen[Kafka]
  }

  final case class KafkaRecord(
    topic: String,
    partition: Int,
    offset: Long,
    timestamp: Long,
    timestampType: String,
    key: String,
    value: String
  )
  object KafkaRecord {
    implicit val decoder: JsonDecoder[KafkaRecord] = DeriveJsonDecoder.gen[KafkaRecord]
  }

  final case class KinesisAnalyticsFirehoseInputPreprocessing(
    invocationId: String,
    applicationArn: String,
    streamArn: String,
    records: List[KinesisAnalyticsFirehoseInputPreprocessingRecord]
  ) extends LambdaEvent
  object KinesisAnalyticsFirehoseInputPreprocessing {
    implicit val decoder: JsonDecoder[KinesisAnalyticsFirehoseInputPreprocessing] =
      DeriveJsonDecoder.gen[KinesisAnalyticsFirehoseInputPreprocessing]
  }

  final case class KinesisAnalyticsFirehoseInputPreprocessingRecord(
    recordId: String,
    kinesisFirehoseRecordMetadata: KinesisFirehoseRecordMetadata,
    data: String // Revisit this as it was defined as java.nio.ByteBuffer
  )
  object KinesisAnalyticsFirehoseInputPreprocessingRecord {
    implicit val decoder: JsonDecoder[KinesisAnalyticsFirehoseInputPreprocessingRecord] =
      DeriveJsonDecoder.gen[KinesisAnalyticsFirehoseInputPreprocessingRecord]
  }
  final case class KinesisFirehoseRecordMetadata(approximateArrivalTimestamp: Long)
  object KinesisFirehoseRecordMetadata {
    implicit val decoder: JsonDecoder[KinesisFirehoseRecordMetadata] =
      DeriveJsonDecoder.gen[KinesisFirehoseRecordMetadata]
  }

  final case class KinesisAnalyticsOutputDelivery(
    invocationId: String,
    applicationArn: String,
    records: List[KinesisAnalyticsOutputDeliveryRecord]
  ) extends LambdaEvent

  object KinesisAnalyticsOutputDelivery {
    implicit val decoder: JsonDecoder[KinesisAnalyticsOutputDelivery] =
      DeriveJsonDecoder.gen[KinesisAnalyticsOutputDelivery]
  }

  final case class KinesisAnalyticsOutputDeliveryRecord(
    recordId: String,
    lambdaDeliveryRecordMetadata: LambdaDeliveryRecordMetadata,
    data: String // Revisit this as it was defined as java.nio.ByteBuffer
  )
  object KinesisAnalyticsOutputDeliveryRecord {
    implicit val decoder: JsonDecoder[KinesisAnalyticsOutputDeliveryRecord] =
      DeriveJsonDecoder.gen[KinesisAnalyticsOutputDeliveryRecord]
  }
  final case class LambdaDeliveryRecordMetadata(retryHint: Long)
  object LambdaDeliveryRecordMetadata {
    implicit val decoder: JsonDecoder[LambdaDeliveryRecordMetadata] =
      DeriveJsonDecoder.gen[LambdaDeliveryRecordMetadata]
  }

  final case class KinesisAnalyticsStreamsInputPreprocessing() extends LambdaEvent
  object KinesisAnalyticsStreamsInputPreprocessing {
    implicit val decoder: JsonDecoder[KinesisAnalyticsStreamsInputPreprocessing] =
      DeriveJsonDecoder.gen[KinesisAnalyticsStreamsInputPreprocessing]
  }

  final case class Kinesis(
    @jsonField("Records") records: List[KinesisRecord]
  ) extends LambdaEvent

  object Kinesis {
    implicit val decoder: JsonDecoder[Kinesis] = DeriveJsonDecoder.gen[Kinesis]
  }

  final case class KinesisRecord(
    kinesis: KinesisRecordUnit,
    eventSource: String,
    eventID: String,
    invokeIdentityArn: String,
    eventName: String,
    eventVersion: String,
    eventSourceARN: String,
    awsRegion: String
  )

  object KinesisRecord {
    implicit val decoder: JsonDecoder[KinesisRecord] = DeriveJsonDecoder.gen[KinesisRecord]
  }

  final case class KinesisRecordUnit(
    kinesisSchemaVersion: String,
    partitionKey: String,
    sequenceNumber: String,
    data: String,
    approximateArrivalTimestamp: java.time.Instant,
    encryptionType: KinesisRecordUnitEncryptionType
  )

  object KinesisRecordUnit {
    implicit val offsetDateTimeDecoder: JsonDecoder[java.time.Instant] = JsonDecoder[Double]
      .map(value => java.time.Instant.ofEpochMilli((BigDecimal(value) * 1000).toLong))

    implicit val decoder: JsonDecoder[KinesisRecordUnit] = DeriveJsonDecoder.gen[KinesisRecordUnit]
  }

  sealed trait KinesisRecordUnitEncryptionType
  object KinesisRecordUnitEncryptionType {
    case object None extends KinesisRecordUnitEncryptionType
    case object Kms  extends KinesisRecordUnitEncryptionType

    implicit val decoder: JsonDecoder[KinesisRecordUnitEncryptionType] = JsonDecoder[String].mapOrFail {
      _.toUpperCase() match {
        case "NONE"  => Right(None)
        case "KMS"   => Right(Kms)
        case unknown => Left(s"Unknown Kinesis event encryptionType: $unknown")
      }
    }
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

  final case class Scheduled(
    id: String,
    account: String,
    region: String,
    detail: Map[String, String],
    source: String,
    resources: List[String],
    time: java.time.ZonedDateTime,
    @jsonField("detail-type") detailType: String
  ) extends LambdaEvent

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
