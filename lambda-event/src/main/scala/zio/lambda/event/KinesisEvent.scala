package zio.lambda.event

import zio.json._

final case class KinesisEvent(
  @jsonField("Records") records: List[KinesisRecord]
)

object KinesisEvent {
  implicit val decoder: JsonDecoder[KinesisEvent] = DeriveJsonDecoder.gen[KinesisEvent]
}

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
  implicit val decoder: JsonDecoder[KinesisRecord] = DeriveJsonDecoder.gen[KinesisRecord]
}

final case class KinesisRecordUnit(
  kinesisSchemaVersion: String,
  partitionKey: String,
  sequenceNumber: String,
  data: String,
  approximateArrivalTimestamp: java.time.Instant,
  encryptionType: KinesisRecordUnitEncryptionType
)

object KinesisRecordUnit {
  implicit val instantDecoder: JsonDecoder[java.time.Instant] = JsonDecoder[Double]
    .map(value => java.time.Instant.ofEpochMilli((BigDecimal(value) * 1000).toLong))

  implicit val decoder: JsonDecoder[KinesisRecordUnit] = DeriveJsonDecoder.gen[KinesisRecordUnit]
}

sealed trait KinesisRecordUnitEncryptionType
object KinesisRecordUnitEncryptionType {
  case object None extends KinesisRecordUnitEncryptionType
  case object Kms  extends KinesisRecordUnitEncryptionType

  implicit val decoder: JsonDecoder[KinesisRecordUnitEncryptionType] = JsonDecoder[String].mapOrFail {
    _.toUpperCase() match {
      case "NONE"  => Right(None)
      case "KMS"   => Right(Kms)
      case unknown => Left(s"Unknown Kinesis event encryptionType: $unknown")
    }
  }
}
