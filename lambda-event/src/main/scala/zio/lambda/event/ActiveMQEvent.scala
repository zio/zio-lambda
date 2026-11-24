package zio.lambda.event

import zio.json._

/** A batch of messages from an Amazon MQ for ActiveMQ broker delivered to a Lambda function. */
final case class ActiveMQEvent(
  eventSource: String,
  eventSourceArn: String,
  messages: List[ActiveMQMessage]
)

object ActiveMQEvent {

  /** Decodes a [[ActiveMQEvent]] from JSON. */
  implicit val decoder: JsonDecoder[ActiveMQEvent] = DeriveJsonDecoder.gen[ActiveMQEvent]
}

/** One message of an [[ActiveMQEvent]]. */
final case class ActiveMQMessage(
  messageID: String,
  messageType: String,
  timestamp: Long,
  deliveryMode: Int,
  correlationID: String,
  replyTo: String,
  destination: ActiveMQMessageDestination,
  redelivered: Boolean,
  `type`: String,
  expiration: Long,
  priority: Int,
  data: String,
  brokerInTime: Long,
  brokerOutTime: Long
)

object ActiveMQMessage {

  /** Decodes a [[ActiveMQMessage]] from JSON. */
  implicit val decoder: JsonDecoder[ActiveMQMessage] = DeriveJsonDecoder.gen[ActiveMQMessage]
}

/** The destination of an [[ActiveMQMessage]]. */
final case class ActiveMQMessageDestination(physicalName: String)

object ActiveMQMessageDestination {

  /** Decodes a [[ActiveMQMessageDestination]] from JSON. */
  implicit val decoder: JsonDecoder[ActiveMQMessageDestination] = DeriveJsonDecoder.gen[ActiveMQMessageDestination]
}
