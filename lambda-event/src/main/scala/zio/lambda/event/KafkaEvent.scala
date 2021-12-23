package zio.lambda.event

import zio.json._

final case class KafkaEvent(
  records: Map[String, List[KafkaRecord]],
  eventSource: String,
  eventSourceArn: String,
  bootstrapServers: String
)

object KafkaEvent {
  implicit val decoder: JsonDecoder[KafkaEvent] = DeriveJsonDecoder.gen[KafkaEvent]
}

final case class KafkaRecord(
  topic: String,
  partition: Int,
  offset: Long,
  timestamp: java.time.Instant,
  timestampType: String,
  key: String,
  value: String
)
object KafkaRecord {
  implicit val instantDecoder: JsonDecoder[java.time.Instant] = JsonDecoder[Long]
    .map(java.time.Instant.ofEpochMilli)

  implicit val decoder: JsonDecoder[KafkaRecord] = DeriveJsonDecoder.gen[KafkaRecord]
}
