package zio.lambda.event

import zio.json._

final case class S3Event()
object S3Event {
  implicit val decoder: JsonDecoder[S3Event] = DeriveJsonDecoder.gen[S3Event]
}
