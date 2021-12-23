package zio.lambda.event

import zio.json._

final case class CodeCommitEvent(records: Seq[CodeCommitRecord])

object CodeCommitEvent {
  implicit val decoder: JsonDecoder[CodeCommitEvent] = DeriveJsonDecoder.gen[CodeCommitEvent]
}

final case class CodeCommitRecord(
  eventId: String,
  eventVersion: String,
  eventTime: java.time.Instant,
  eventTriggerName: String,
  eventPartNumber: Int,
  codeCommit: CodeCommitRecordCommit,
  eventName: String,
  eventTriggerConfigId: String,
  eventSourceArn: String,
  userIdentityArn: String,
  eventSource: String,
  awsRegion: String,
  customData: String,
  eventTotalParts: Int
)
object CodeCommitRecord {
  implicit val decoder: JsonDecoder[CodeCommitRecord] = DeriveJsonDecoder.gen[CodeCommitRecord]
}

final case class CodeCommitRecordCommit(references: List[CodeCommitRecordCommitReference])
object CodeCommitRecordCommit {
  implicit val decoder: JsonDecoder[CodeCommitRecordCommit] = DeriveJsonDecoder.gen[CodeCommitRecordCommit]
}

final case class CodeCommitRecordCommitReference(
  commit: String,
  ref: String,
  created: Boolean
)
object CodeCommitRecordCommitReference {
  implicit val decoder: JsonDecoder[CodeCommitRecordCommitReference] =
    DeriveJsonDecoder.gen[CodeCommitRecordCommitReference]
}
