package zio.lambda.event

import zio.json._

final case class ActiveMQEvent(
  eventSource: String,
  eventSourceArn: String,
  messages: List[ActiveMQMessage]
)

object ActiveMQEvent {
  implicit val decoder: JsonDecoder[ActiveMQEvent] = DeriveJsonDecoder.gen[ActiveMQEvent]
}

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
  implicit val decoder: JsonDecoder[ActiveMQMessage] = DeriveJsonDecoder.gen[ActiveMQMessage]
}

final case class ActiveMQMessageDestination(physicalName: String)

object ActiveMQMessageDestination {
  implicit val decoder: JsonDecoder[ActiveMQMessageDestination] = DeriveJsonDecoder.gen[ActiveMQMessageDestination]
}
