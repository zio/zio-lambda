package zio.lambda

import zio.random.Random
import zio.test._

object InvocationRequestGen {

  private val genClient =
    for {
      installationId <- Gen.anyString
      appTitle       <- Gen.anyString
      appVersionName <- Gen.anyString
      appVersionCode <- Gen.anyString
      appPackageName <- Gen.anyString
    } yield InvocationRequest.ClientContext.Client(
      installationId = installationId,
      appTitle = appTitle,
      appVersionName = appVersionName,
      appVersionCode = appVersionCode,
      appPackageName = appPackageName
    )

  private val genClientContext =
    for {
      client <- genClient
      custom <- Gen.mapOf(Gen.anyString, Gen.anyString)
      env    <- Gen.mapOf(Gen.anyString, Gen.anyString)
    } yield InvocationRequest.ClientContext(
      client = client,
      custom = custom,
      env = env
    )

  private val genCognitoIdentity =
    for {
      cognitoIdentityId     <- Gen.anyString
      cognitoIdentityPoolId <- Gen.anyString
    } yield InvocationRequest.CognitoIdentity(
      cognitoIdentityId = cognitoIdentityId,
      cognitoIdentityPoolId = cognitoIdentityPoolId
    )

  val gen: Gen[Random with Sized, InvocationRequest] =
    for {
      id                 <- Gen.anyString
      deadlineMs         <- Gen.anyLong
      invokedFunctionArn <- Gen.anyString
      xrayTraceId        <- Gen.anyString
      clientContext      <- Gen.option(genClientContext)
      cognitoIdentity    <- Gen.option(genCognitoIdentity)
      payload            <- Gen.anyString
    } yield InvocationRequest(
      id = InvocationRequest.Id(id),
      deadlineMs = deadlineMs,
      invokedFunctionArn = invokedFunctionArn,
      xrayTraceId = xrayTraceId,
      clientContext = clientContext,
      cognitoIdentity = cognitoIdentity,
      payload = payload
    )

}
