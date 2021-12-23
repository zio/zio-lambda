package zio.lambda.event

import zio.json._

final case class CognitoUserPoolEvent()

object CognitoUserPoolEvent {
  implicit val decoder: JsonDecoder[CognitoUserPoolEvent] = DeriveJsonDecoder.gen[CognitoUserPoolEvent]
}
