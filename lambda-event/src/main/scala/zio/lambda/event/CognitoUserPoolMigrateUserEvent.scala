package zio.lambda.event

import zio.json._

final case class CognitoUserPoolMigrateUserEvent()
object CognitoUserPoolMigrateUserEvent {
  implicit val decoder: JsonDecoder[CognitoUserPoolMigrateUserEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolMigrateUserEvent]
}
