package zio.lambda.event

import zio.json._

final case class LambdaDestinationEvent()

object LambdaDestinationEvent {
  implicit val decoder: JsonDecoder[LambdaDestinationEvent] = DeriveJsonDecoder.gen[LambdaDestinationEvent]
}
