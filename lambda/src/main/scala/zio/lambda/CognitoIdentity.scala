package zio.lambda

import zio.json._

/**
 * The Amazon Cognito identity of an invocation from the AWS Mobile SDK. The runner puts it in [[Context]] as `cognitoIdentity`.
 *
 * @param cognitoIdentityId the Cognito identity ID
 * @param cognitoIdentityPoolId the ID of the Cognito identity pool
 */
final case class CognitoIdentity(cognitoIdentityId: String, cognitoIdentityPoolId: String)

/** Holds the JSON decoder of [[CognitoIdentity]]. */
object CognitoIdentity {

  /** Decodes a JSON object with the keys `cognitoIdentityId` and `cognitoIdentityPoolId`. */
  implicit val decoder: JsonDecoder[CognitoIdentity] = DeriveJsonDecoder.gen[CognitoIdentity]
}
