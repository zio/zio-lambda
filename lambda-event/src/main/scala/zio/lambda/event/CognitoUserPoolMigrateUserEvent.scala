package zio.lambda.event

import zio.json._

/**
 * An Amazon Cognito user pool Migrate User trigger event. A placeholder with no fields: the decoder accepts any JSON value and discards it.
 */
final case class CognitoUserPoolMigrateUserEvent()
object CognitoUserPoolMigrateUserEvent {

  /** Decodes a [[CognitoUserPoolMigrateUserEvent]] from JSON. */
  implicit val decoder: JsonDecoder[CognitoUserPoolMigrateUserEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolMigrateUserEvent]
}
