package zio.lambda.event

import zio.json._

/** An Amazon Connect contact flow invocation event delivered to a Lambda function. */
final case class ConnectEvent(details: ConnectDetails, name: String)

object ConnectEvent {

  /** Decodes a [[ConnectEvent]] from JSON. */
  implicit val decoder: JsonDecoder[ConnectEvent] = DeriveJsonDecoder.gen[ConnectEvent]
}

/** The details of a [[ConnectEvent]]: the contact data and the flow parameters. */
final case class ConnectDetails(contactData: ConnectContactData, parameters: Map[String, String])
object ConnectDetails {

  /** Decodes a [[ConnectDetails]] from JSON. */
  implicit val decoder: JsonDecoder[ConnectDetails] = DeriveJsonDecoder.gen[ConnectDetails]
}

/** The contact data of a [[ConnectDetails]]. */
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

  /** Decodes a [[ConnectContactData]] from JSON. */
  implicit val decoder: JsonDecoder[ConnectContactData] = DeriveJsonDecoder.gen[ConnectContactData]
}

/** An Amazon Connect endpoint, with an address and a type, of a [[ConnectContactData]]. */
final case class Endpoint(address: String, `type`: String)
object Endpoint {

  /** Decodes a [[Endpoint]] from JSON. */
  implicit val decoder: JsonDecoder[Endpoint] = DeriveJsonDecoder.gen[Endpoint]
}
