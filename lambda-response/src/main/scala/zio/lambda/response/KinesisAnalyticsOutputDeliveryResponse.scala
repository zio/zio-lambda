package zio.lambda.response

import zio.json._

final case class KinesisAnalyticsOutputDeliveryResponse(records: List[KinesisAnalyticsOutputDeliveryResponseRecord])

object KinesisAnalyticsOutputDeliveryResponse {
  implicit val encoder: JsonEncoder[KinesisAnalyticsOutputDeliveryResponse] =
    DeriveJsonEncoder.gen[KinesisAnalyticsOutputDeliveryResponse]

}

final case class KinesisAnalyticsOutputDeliveryResponseRecord(
  recordId: String,
  result: KinesisAnalyticsOutputDeliveryResponseRecordResult
)

object KinesisAnalyticsOutputDeliveryResponseRecord {
  implicit val encoder: JsonEncoder[KinesisAnalyticsOutputDeliveryResponseRecord] =
    DeriveJsonEncoder.gen[KinesisAnalyticsOutputDeliveryResponseRecord]

}

sealed trait KinesisAnalyticsOutputDeliveryResponseRecordResult
object KinesisAnalyticsOutputDeliveryResponseRecordResult {
  implicit val encoder: JsonEncoder[KinesisAnalyticsOutputDeliveryResponseRecordResult] =
    DeriveJsonEncoder.gen[KinesisAnalyticsOutputDeliveryResponseRecordResult]

  case object Ok             extends KinesisAnalyticsOutputDeliveryResponseRecordResult
  case object DeliveryFailed extends KinesisAnalyticsOutputDeliveryResponseRecordResult
}
