package zio.lambda.event

import zio.json._

final case class CognitoUserPoolPreTokenGenerationEvent()
object CognitoUserPoolPreTokenGenerationEvent {
  implicit val decoder: JsonDecoder[CognitoUserPoolPreTokenGenerationEvent] =
    DeriveJsonDecoder.gen[CognitoUserPoolPreTokenGenerationEvent]
}
