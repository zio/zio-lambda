package zio.lambda.internal

import zio.Random
import zio.test._
import zio.lambda.ClientContext
import zio.lambda.CognitoIdentity
import zio.lambda.Client

object InvocationRequestGen {

  private val genClient =
    for {
      installationId <- Gen.string
      appTitle       <- Gen.string
      appVersionName <- Gen.string
      appVersionCode <- Gen.string
      appPackageName <- Gen.string
    } yield Client(
      installationId = installationId,
      appTitle = appTitle,
      appVersionName = appVersionName,
      appVersionCode = appVersionCode,
      appPackageName = appPackageName
    )

  private val genClientContext =
    for {
      client <- genClient
      custom <- Gen.mapOf(Gen.string, Gen.string)
      env    <- Gen.mapOf(Gen.string, Gen.string)
    } yield ClientContext(
      client = client,
      custom = custom,
      env = env
    )

  private val genCognitoIdentity =
    for {
      cognitoIdentityId     <- Gen.string
      cognitoIdentityPoolId <- Gen.string
    } yield CognitoIdentity(
      cognitoIdentityId = cognitoIdentityId,
      cognitoIdentityPoolId = cognitoIdentityPoolId
    )

  val gen: Gen[Random with Sized, InvocationRequest] =
    for {
      id                    <- Gen.string
      remainingTimeInMillis <- Gen.long
      invokedFunctionArn    <- Gen.string
      xrayTraceId           <- Gen.string
      clientContext         <- Gen.option(genClientContext)
      cognitoIdentity       <- Gen.option(genCognitoIdentity)
      payload               <- Gen.string
    } yield InvocationRequest(
      id = id,
      remainingTimeInMillis = remainingTimeInMillis,
      invokedFunctionArn = invokedFunctionArn,
      xrayTraceId = xrayTraceId,
      clientContext = clientContext,
      cognitoIdentity = cognitoIdentity,
      payload = payload
    )

}
