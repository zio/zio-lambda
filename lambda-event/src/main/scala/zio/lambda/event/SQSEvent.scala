package zio.lambda.event

import zio.json._

final case class SQSEvent(@jsonField("Records") records: List[SQSRecord])

object SQSEvent {
  implicit val decoder: JsonDecoder[SQSEvent] = DeriveJsonDecoder.gen[SQSEvent]
}

final case class SQSRecord(
  messageId: String,
  receiptHandle: String,
  body: String,
  md5OfBody: String,
  md5OfMessageAttributes: String,
  eventSourceARN: String,
  eventSource: String,
  awsRegion: String,
  attributes: Map[String, String],
  messageAttributes: Map[String, SQSMessageAttribute]
)

object SQSRecord {
  implicit val decoder: JsonDecoder[SQSRecord] = DeriveJsonDecoder.gen[SQSRecord]
}

final case class SQSMessageAttribute(stringValue: String, dataType: SQSMessageAttributeDataType)
object SQSMessageAttribute {
  implicit val decoder: JsonDecoder[SQSMessageAttribute] = DeriveJsonDecoder.gen[SQSMessageAttribute]

}

sealed trait SQSMessageAttributeDataType
object SQSMessageAttributeDataType {
  implicit val decoder: JsonDecoder[SQSMessageAttributeDataType] = JsonDecoder[String].mapOrFail {
    _.toUpperCase() match {
      case "STRING" => Right(String)
      case "NUMBER" => Right(Number)
      case "BINARY" => Right(Binary)
      case value    => Left(s"Unknown SQSMessageAttributeDataType: $value")
    }
  }

  case object String extends SQSMessageAttributeDataType
  case object Number extends SQSMessageAttributeDataType
  case object Binary extends SQSMessageAttributeDataType
}
