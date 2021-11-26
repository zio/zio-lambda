package zio.lambda.internal

import zio.random.Random
import zio.test._
import zio.lambda.ClientContext
import zio.lambda.CognitoIdentity

object InvocationRequestGen {

  private val genClient =
    for {
      installationId <- Gen.anyString
      appTitle       <- Gen.anyString
      appVersionName <- Gen.anyString
      appVersionCode <- Gen.anyString
      appPackageName <- Gen.anyString
    } yield ClientContext.Client(
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
    } yield ClientContext(
      client = client,
      custom = custom,
      env = env
    )

  private val genCognitoIdentity =
    for {
      cognitoIdentityId     <- Gen.anyString
      cognitoIdentityPoolId <- Gen.anyString
    } yield CognitoIdentity(
      cognitoIdentityId = cognitoIdentityId,
      cognitoIdentityPoolId = cognitoIdentityPoolId
    )

  val gen: Gen[Random with Sized, InvocationRequest] =
    for {
      id                    <- Gen.anyString
      remainingTimeInMillis <- Gen.option(Gen.anyInt)
      invokedFunctionArn    <- Gen.option(Gen.anyString)
      xrayTraceId           <- Gen.option(Gen.anyString)
      clientContext         <- Gen.option(genClientContext)
      cognitoIdentity       <- Gen.option(genCognitoIdentity)
      payload               <- Gen.anyString
    } yield InvocationRequest(
      id = InvocationRequest.Id(id),
      remainingTimeInMillis = remainingTimeInMillis,
      invokedFunctionArn = invokedFunctionArn,
      xrayTraceId = xrayTraceId,
      clientContext = clientContext,
      cognitoIdentity = cognitoIdentity,
      payload = payload
    )

}
