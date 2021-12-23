package zio.lambda.event

import zio.json._

final case class ScheduledEvent(
  id: String,
  account: String,
  region: String,
  detail: Map[String, String],
  source: String,
  resources: List[String],
  time: java.time.ZonedDateTime,
  @jsonField("detail-type") detailType: String
)

object ScheduledEvent {
  implicit val decoder: JsonDecoder[ScheduledEvent] = DeriveJsonDecoder.gen[ScheduledEvent]
}
