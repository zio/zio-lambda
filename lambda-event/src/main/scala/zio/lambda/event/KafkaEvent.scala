package zio.lambda.event

import zio.json._

/** A batch of records from an Amazon MSK or self-managed Apache Kafka event source delivered to a Lambda function. */
final case class KafkaEvent(
  records: Map[String, List[KafkaRecord]],
  eventSource: String,
  eventSourceArn: String,
  bootstrapServers: String
)

object KafkaEvent {

  /** Decodes a [[KafkaEvent]] from JSON. */
  implicit val decoder: JsonDecoder[KafkaEvent] = DeriveJsonDecoder.gen[KafkaEvent]
}

/**
 * One record of a [[KafkaEvent]].
 *
 * @param timestamp The record time; read from epoch milliseconds.
 */
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

  /** Decodes a `java.time.Instant` from a JSON number of epoch milliseconds. */
  implicit val instantDecoder: JsonDecoder[java.time.Instant] = JsonDecoder[Long]
    .map(java.time.Instant.ofEpochMilli)

  /** Decodes a [[KafkaRecord]] from JSON. */
  implicit val decoder: JsonDecoder[KafkaRecord] = DeriveJsonDecoder.gen[KafkaRecord]
}
