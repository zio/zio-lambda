package zio.lambda.event

import zio.json._

/** An AWS CodeCommit repository trigger event delivered to a Lambda function. */
final case class CodeCommitEvent(records: Seq[CodeCommitRecord])

object CodeCommitEvent {

  /** Decodes a [[CodeCommitEvent]] from JSON. */
  implicit val decoder: JsonDecoder[CodeCommitEvent] = DeriveJsonDecoder.gen[CodeCommitEvent]
}

/** One record of a [[CodeCommitEvent]]. */
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

  /** Decodes a [[CodeCommitRecord]] from JSON. */
  implicit val decoder: JsonDecoder[CodeCommitRecord] = DeriveJsonDecoder.gen[CodeCommitRecord]
}

/** The `codeCommit` object of a [[CodeCommitRecord]], listing the references that the event touches. */
final case class CodeCommitRecordCommit(references: List[CodeCommitRecordCommitReference])
object CodeCommitRecordCommit {

  /** Decodes a [[CodeCommitRecordCommit]] from JSON. */
  implicit val decoder: JsonDecoder[CodeCommitRecordCommit] = DeriveJsonDecoder.gen[CodeCommitRecordCommit]
}

/**
 * One reference (`ref`) of a [[CodeCommitRecordCommit]], with its commit ID and a flag for whether it was created.
 */
final case class CodeCommitRecordCommitReference(
  commit: String,
  ref: String,
  created: Boolean
)
object CodeCommitRecordCommitReference {

  /** Decodes a [[CodeCommitRecordCommitReference]] from JSON. */
  implicit val decoder: JsonDecoder[CodeCommitRecordCommitReference] =
    DeriveJsonDecoder.gen[CodeCommitRecordCommitReference]
}
