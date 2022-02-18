package zio.lambda.internal

import zio.json._
import zio.lambda.ClientContext
import zio.lambda.CognitoIdentity
import zio.lambda.Client

object InvocationRequestImplicits {
  implicit val clientEncoder: JsonEncoder[Client] =
    DeriveJsonEncoder.gen[Client]

  implicit val clientContextEncoder: JsonEncoder[ClientContext] =
    DeriveJsonEncoder.gen[ClientContext]

  implicit val cognitoIdentityEncoder: JsonEncoder[CognitoIdentity] =
    DeriveJsonEncoder.gen[CognitoIdentity]

  implicit val invocationRequestEncoder: JsonEncoder[InvocationRequest] = DeriveJsonEncoder.gen[InvocationRequest]
}
