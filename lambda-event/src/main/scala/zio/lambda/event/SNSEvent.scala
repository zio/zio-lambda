package zio.lambda.event

import zio.json._

final case class SNSEvent()
object SNSEvent {
  implicit val decoder: JsonDecoder[SNSEvent] = DeriveJsonDecoder.gen[SNSEvent]
}
