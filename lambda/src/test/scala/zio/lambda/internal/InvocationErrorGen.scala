package zio.lambda.internal

import zio.random.Random
import zio.test._

object InvocationErrorGen {

  private val genInvocationErrorResponse =
    for {
      error      <- Gen.anyString
      errorType  <- Gen.anyString
      stackTrace <- Gen.listOf(Gen.anyString)
    } yield InvocationErrorResponse(
      error,
      errorType,
      stackTrace
    )

  val gen: Gen[Random with Sized, InvocationError] =
    for {
      requestId               <- Gen.anyString
      invocationErrorResponse <- genInvocationErrorResponse
    } yield InvocationError(
      InvocationRequest.Id(requestId),
      invocationErrorResponse
    )

}
