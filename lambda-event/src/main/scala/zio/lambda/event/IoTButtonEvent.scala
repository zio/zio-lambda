package zio.lambda.event

import zio.json._

final case class IoTButtonEvent(
  serialNumber: String,
  clickType: String,
  batteryVoltage: String
)
object IoTButtonEvent {
  implicit val decoder: JsonDecoder[IoTButtonEvent] = DeriveJsonDecoder.gen[IoTButtonEvent]
}
