package zio.lambda.event

import zio.json._

final case class CognitoUserPoolPreSignUpEvent()
object CognitoUserPoolPreSignUpEvent {
  implicit val decoder: JsonDecoder[CognitoUserPoolPreSignUpEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolPreSignUpEvent]
}
