package zio.lambda

import zio.json._

final case class CognitoIdentity(cognitoIdentityId: String, cognitoIdentityPoolId: String)

object CognitoIdentity {
  implicit val decoder: JsonDecoder[CognitoIdentity] = DeriveJsonDecoder.gen[CognitoIdentity]
}
