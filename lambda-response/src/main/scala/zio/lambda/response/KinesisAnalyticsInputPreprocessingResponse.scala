package zio.lambda.response

import zio.json._

final case class KinesisAnalyticsInputPreprocessingResponse(
  records: List[KinesisAnalyticsInputPreprocessingResponseRecord]
)

object KinesisAnalyticsInputPreprocessingResponse {
  implicit val encoder: JsonEncoder[KinesisAnalyticsInputPreprocessingResponse] =
    DeriveJsonEncoder.gen[KinesisAnalyticsInputPreprocessingResponse]

}

final case class KinesisAnalyticsInputPreprocessingResponseRecord(
  recordId: String,
  result: KinesisAnalyticsInputPreprocessingResponseRecordResult
)

object KinesisAnalyticsInputPreprocessingResponseRecord {
  implicit val encoder: JsonEncoder[KinesisAnalyticsInputPreprocessingResponseRecord] =
    DeriveJsonEncoder.gen[KinesisAnalyticsInputPreprocessingResponseRecord]

}

sealed trait KinesisAnalyticsInputPreprocessingResponseRecordResult
object KinesisAnalyticsInputPreprocessingResponseRecordResult {
  implicit val encoder: JsonEncoder[KinesisAnalyticsInputPreprocessingResponseRecordResult] =
    DeriveJsonEncoder.gen[KinesisAnalyticsInputPreprocessingResponseRecordResult]

  case object Ok               extends KinesisAnalyticsInputPreprocessingResponseRecordResult
  case object ProcessingFailed extends KinesisAnalyticsInputPreprocessingResponseRecordResult
  case object Dropped          extends KinesisAnalyticsInputPreprocessingResponseRecordResult
}
