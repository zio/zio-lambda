package zio.lambda.event

import zio.json._

/** An Amazon Cognito Sync trigger event delivered to a Lambda function. */
final case class CognitoEvent(
  region: String,
  datasetRecords: Map[String, CognitoDatasetRecord],
  identityPoolId: String,
  identityId: String,
  datasetName: String,
  eventType: String,
  version: Int
)

object CognitoEvent {

  /** Decodes a [[CognitoEvent]] from JSON. */
  implicit val decoder: JsonDecoder[CognitoEvent] = DeriveJsonDecoder.gen[CognitoEvent]
}

/** One dataset record of a [[CognitoEvent]], with its old value, new value and operation. */
final case class CognitoDatasetRecord(
  oldValue: String,
  newValue: String,
  op: String
)
object CognitoDatasetRecord {

  /** Decodes a [[CognitoDatasetRecord]] from JSON. */
  implicit val decoder: JsonDecoder[CognitoDatasetRecord] = DeriveJsonDecoder.gen[CognitoDatasetRecord]
}
