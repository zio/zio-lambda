package zio.lambda.event

import zio.json._

/** An Amazon EventBridge (CloudWatch Events) scheduled event delivered to a Lambda function. */
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

  /** Decodes a [[ScheduledEvent]] from JSON. */
  implicit val decoder: JsonDecoder[ScheduledEvent] = DeriveJsonDecoder.gen[ScheduledEvent]
}
