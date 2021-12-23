package zio.lambda.event

import zio.json._

final case class ConnectEvent(details: ConnectDetails, name: String)

object ConnectEvent {
  implicit val decoder: JsonDecoder[ConnectEvent] = DeriveJsonDecoder.gen[ConnectEvent]
}

final case class ConnectDetails(contactData: ConnectContactData, parameters: Map[String, String])
object ConnectDetails {
  implicit val decoder: JsonDecoder[ConnectDetails] = DeriveJsonDecoder.gen[ConnectDetails]
}

final case class ConnectContactData(
  attributes: Map[String, String],
  channel: String,
  contactId: String,
  customerEndpoint: Endpoint,
  initialContactId: String,
  initiationMethod: String,
  instanceArn: String,
  previousContactId: String,
  queue: String,
  systemEndpoint: Endpoint
)
object ConnectContactData {
  implicit val decoder: JsonDecoder[ConnectContactData] = DeriveJsonDecoder.gen[ConnectContactData]
}
final case class Endpoint(address: String, `type`: String)
object Endpoint {
  implicit val decoder: JsonDecoder[Endpoint] = DeriveJsonDecoder.gen[Endpoint]
}
