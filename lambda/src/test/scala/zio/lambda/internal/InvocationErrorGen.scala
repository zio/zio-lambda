package zio.lambda.internal

import zio.Random
import zio.test._

object InvocationErrorGen {

  private val genInvocationErrorResponse =
    for {
      error      <- Gen.string
      errorType  <- Gen.string
      stackTrace <- Gen.listOf(Gen.string)
    } yield InvocationErrorResponse(
      error,
      errorType,
      stackTrace
    )

  val gen: Gen[Random with Sized, InvocationError] =
    for {
      requestId               <- Gen.string
      invocationErrorResponse <- genInvocationErrorResponse
    } yield InvocationError(
      requestId,
      invocationErrorResponse
    )

}
