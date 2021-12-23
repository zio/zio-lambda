package zio.lambda.event

import zio.json._

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
  implicit val decoder: JsonDecoder[CognitoEvent] = DeriveJsonDecoder.gen[CognitoEvent]
}

final case class CognitoDatasetRecord(
  oldValue: String,
  newValue: String,
  op: String
)
object CognitoDatasetRecord {
  implicit val decoder: JsonDecoder[CognitoDatasetRecord] = DeriveJsonDecoder.gen[CognitoDatasetRecord]
}
