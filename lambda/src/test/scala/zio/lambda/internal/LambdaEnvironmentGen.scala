package zio.lambda.internal

import zio.random.Random
import zio.test._

object LambdaEnvironmentGen {

  val gen: Gen[Random with Sized, LambdaEnvironment] =
    for {
      runtimeApi      <- Gen.string(Gen.anyChar)
      handler         <- Gen.anyString
      taskRoot        <- Gen.anyString
      memoryLimitInMB <- Gen.anyInt
      logGroupName    <- Gen.anyString
      logStreamName   <- Gen.anyString
      functionName    <- Gen.anyString
      functionVersion <- Gen.anyString
    } yield LambdaEnvironment(
      runtimeApi,
      handler,
      taskRoot,
      memoryLimitInMB,
      logGroupName,
      logStreamName,
      functionName,
      functionVersion
    )
}
