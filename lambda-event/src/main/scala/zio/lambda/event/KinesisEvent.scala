package zio.lambda.event

import zio.json._

/** A batch of records from an Amazon Kinesis data stream delivered to a Lambda function. */
final case class KinesisEvent(
  @jsonField("Records") records: List[KinesisRecord]
)

object KinesisEvent {

  /** Decodes a [[KinesisEvent]] from JSON. */
  implicit val decoder: JsonDecoder[KinesisEvent] = DeriveJsonDecoder.gen[KinesisEvent]
}

/** One record of a [[KinesisEvent]]. */
final case class KinesisRecord(
  kinesis: KinesisRecordUnit,
  eventSource: String,
  eventID: String,
  invokeIdentityArn: String,
  eventName: String,
  eventVersion: String,
  eventSourceARN: String,
  awsRegion: String
)

object KinesisRecord {

  /** Decodes a [[KinesisRecord]] from JSON. */
  implicit val decoder: JsonDecoder[KinesisRecord] = DeriveJsonDecoder.gen[KinesisRecord]
}

/**
 * The `kinesis` object of a [[KinesisRecord]]: the schema version, partition key, sequence number, data, arrival time and encryption type.
 *
 * @param approximateArrivalTimestamp The arrival time; read from epoch seconds.
 */
final case class KinesisRecordUnit(
  kinesisSchemaVersion: String,
  partitionKey: String,
  sequenceNumber: String,
  data: String,
  approximateArrivalTimestamp: java.time.Instant,
  encryptionType: KinesisRecordUnitEncryptionType
)

object KinesisRecordUnit {

  /** Decodes a `java.time.Instant` from a JSON number of epoch seconds, which may have a fractional part. */
  implicit val instantDecoder: JsonDecoder[java.time.Instant] = JsonDecoder[Double]
    .map(value => java.time.Instant.ofEpochMilli((BigDecimal(value) * 1000).toLong))

  /** Decodes a [[KinesisRecordUnit]] from JSON. */
  implicit val decoder: JsonDecoder[KinesisRecordUnit] = DeriveJsonDecoder.gen[KinesisRecordUnit]
}

/**
 * The server-side encryption type of a [[KinesisRecordUnit]]: [[KinesisRecordUnitEncryptionType.None]] or [[KinesisRecordUnitEncryptionType.Kms]].
 */
sealed trait KinesisRecordUnitEncryptionType
object KinesisRecordUnitEncryptionType {

  /** The record is not encrypted. */
  case object None extends KinesisRecordUnitEncryptionType

  /** The record is encrypted with an AWS KMS key. */
  case object Kms extends KinesisRecordUnitEncryptionType

  /** Decodes a [[KinesisRecordUnitEncryptionType]] from JSON. */
  implicit val decoder: JsonDecoder[KinesisRecordUnitEncryptionType] = JsonDecoder[String].mapOrFail {
    _.toUpperCase() match {
      case "NONE"  => Right(None)
      case "KMS"   => Right(Kms)
      case unknown => Left(s"Unknown Kinesis event encryptionType: $unknown")
    }
  }
}
