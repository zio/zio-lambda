package zio.lambda.internal

import zio.json._
import zio.lambda.ClientContext
import zio.lambda.CognitoIdentity

object InvocationRequestImplicits {
  implicit val idEncoder: JsonEncoder[InvocationRequest.Id] = DeriveJsonEncoder.gen[InvocationRequest.Id]

  implicit val clientEncoder: JsonEncoder[ClientContext.Client] =
    DeriveJsonEncoder.gen[ClientContext.Client]

  implicit val clientContextEncoder: JsonEncoder[ClientContext] =
    DeriveJsonEncoder.gen[ClientContext]

  implicit val cognitoIdentityEncoder: JsonEncoder[CognitoIdentity] =
    DeriveJsonEncoder.gen[CognitoIdentity]

  implicit val invocationRequestEncoder: JsonEncoder[InvocationRequest] = DeriveJsonEncoder.gen[InvocationRequest]
}
