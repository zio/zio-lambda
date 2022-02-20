package zio.lambda.internal

import zio.Random
import zio.test._

object LambdaEnvironmentGen {

  val gen: Gen[Random with Sized, LambdaEnvironment] =
    for {
      runtimeApi      <- Gen.string(Gen.char)
      handler         <- Gen.string
      taskRoot        <- Gen.string
      memoryLimitInMB <- Gen.int
      logGroupName    <- Gen.string
      logStreamName   <- Gen.string
      functionName    <- Gen.string
      functionVersion <- Gen.string
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
