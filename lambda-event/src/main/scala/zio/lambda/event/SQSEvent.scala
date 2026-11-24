package zio.lambda.event

import zio.json._

/** A batch of Amazon SQS messages delivered to a Lambda function. */
final case class SQSEvent(@jsonField("Records") records: List[SQSRecord])

object SQSEvent {

  /** Decodes a [[SQSEvent]] from JSON. */
  implicit val decoder: JsonDecoder[SQSEvent] = DeriveJsonDecoder.gen[SQSEvent]
}

/**
 * One message of an [[SQSEvent]].
 *
 * @param md5OfMessageAttributes `None` when the JSON field is missing or `null`, which SQS does for a message with no message attributes.
 */
final case class SQSRecord(
  messageId: String,
  receiptHandle: String,
  body: String,
  md5OfBody: String,
  md5OfMessageAttributes: Option[String],
  eventSourceARN: String,
  eventSource: String,
  awsRegion: String,
  attributes: Map[String, String],
  messageAttributes: Map[String, SQSMessageAttribute]
)

object SQSRecord {

  /** Decodes a [[SQSRecord]] from JSON. */
  implicit val decoder: JsonDecoder[SQSRecord] = DeriveJsonDecoder.gen[SQSRecord]
}

/** A message attribute of an [[SQSRecord]]. */
final case class SQSMessageAttribute(stringValue: String, dataType: SQSMessageAttributeDataType)
object SQSMessageAttribute {

  /** Decodes a [[SQSMessageAttribute]] from JSON. */
  implicit val decoder: JsonDecoder[SQSMessageAttribute] = DeriveJsonDecoder.gen[SQSMessageAttribute]

}

/**
 * The data type of an [[SQSMessageAttribute]]: [[SQSMessageAttributeDataType.String]], [[SQSMessageAttributeDataType.Number]] or [[SQSMessageAttributeDataType.Binary]]. The decoder matches the JSON string case-insensitively.
 */
sealed trait SQSMessageAttributeDataType
object SQSMessageAttributeDataType {

  /** Decodes a [[SQSMessageAttributeDataType]] from JSON. */
  implicit val decoder: JsonDecoder[SQSMessageAttributeDataType] = JsonDecoder[String].mapOrFail {
    _.toUpperCase() match {
      case "STRING" => Right(String)
      case "NUMBER" => Right(Number)
      case "BINARY" => Right(Binary)
      case value    => Left(s"Unknown SQSMessageAttributeDataType: $value")
    }
  }

  /** A string attribute. */
  case object String extends SQSMessageAttributeDataType

  /** A numeric attribute. */
  case object Number extends SQSMessageAttributeDataType

  /** A binary attribute. */
  case object Binary extends SQSMessageAttributeDataType
}
