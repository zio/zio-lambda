package zio.lambda.event

import zio.json._

/** An AWS IoT button click event delivered to a Lambda function. */
final case class IoTButtonEvent(
  serialNumber: String,
  clickType: String,
  batteryVoltage: String
)
object IoTButtonEvent {

  /** Decodes a [[IoTButtonEvent]] from JSON. */
  implicit val decoder: JsonDecoder[IoTButtonEvent] = DeriveJsonDecoder.gen[IoTButtonEvent]
}
