package zio.lambda.event

import zio.json._

final case class S3BatchEvent()
object S3BatchEvent {
  implicit val decoder: JsonDecoder[S3BatchEvent] = DeriveJsonDecoder.gen[S3BatchEvent]
}
