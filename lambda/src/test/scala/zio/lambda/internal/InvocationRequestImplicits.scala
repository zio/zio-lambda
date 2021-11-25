package zio.lambda.internal

import zio.json._

object InvocationRequestImplicits {
  implicit val idEncoder: JsonEncoder[InvocationRequest.Id] = DeriveJsonEncoder.gen[InvocationRequest.Id]

  implicit val clientEncoder: JsonEncoder[InvocationRequest.ClientContext.Client] =
    DeriveJsonEncoder.gen[InvocationRequest.ClientContext.Client]

  implicit val clientContextEncoder: JsonEncoder[InvocationRequest.ClientContext] =
    DeriveJsonEncoder.gen[InvocationRequest.ClientContext]

  implicit val cognitoIdentityEncoder: JsonEncoder[InvocationRequest.CognitoIdentity] =
    DeriveJsonEncoder.gen[InvocationRequest.CognitoIdentity]

  implicit val invocationRequestEncoder: JsonEncoder[InvocationRequest] = DeriveJsonEncoder.gen[InvocationRequest]
}
