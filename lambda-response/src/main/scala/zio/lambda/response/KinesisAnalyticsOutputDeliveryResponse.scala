package zio.lambda.response

import zio.json._

/**
 * The response that a Kinesis Data Analytics output delivery function returns: one result per delivered record,
 * matched by `recordId`. Return it from a handler that receives a
 * `zio.lambda.event.KinesisAnalyticsOutputDeliveryEvent`.
 *
 * The encoding of the record results differs between Scala versions. On Scala 2 each result is written as an object
 * keyed by the case name, `{"Ok":{}}`, and on Scala 3 as the case name in a string, `"Ok"`. Kinesis Data Analytics
 * expects the string form.
 *
 * {{{
 * import KinesisAnalyticsOutputDeliveryResponseRecordResult._
 *
 * val response = KinesisAnalyticsOutputDeliveryResponse(
 *   List(
 *     KinesisAnalyticsOutputDeliveryResponseRecord("output-7", Ok),
 *     KinesisAnalyticsOutputDeliveryResponseRecord("output-8", DeliveryFailed)
 *   )
 * )
 * }}}
 *
 * @param records the result for each delivered record
 *
 * @see [[https://zio.dev/zio-lambda/ ZIO Lambda]]
 */
final case class KinesisAnalyticsOutputDeliveryResponse(records: List[KinesisAnalyticsOutputDeliveryResponseRecord])

/** Holds the JSON encoder of [[KinesisAnalyticsOutputDeliveryResponse]]. */
object KinesisAnalyticsOutputDeliveryResponse {

  /**
   * Encodes the response as a JSON object with a `records` array.
   */
  implicit val encoder: JsonEncoder[KinesisAnalyticsOutputDeliveryResponse] =
    DeriveJsonEncoder.gen[KinesisAnalyticsOutputDeliveryResponse]

}

/**
 * The result of delivering one output record.
 *
 * @param recordId the ID of the delivered record that this result answers
 * @param result whether the delivery succeeded or failed
 */
final case class KinesisAnalyticsOutputDeliveryResponseRecord(
  recordId: String,
  result: KinesisAnalyticsOutputDeliveryResponseRecordResult
)

/** Holds the JSON encoder of [[KinesisAnalyticsOutputDeliveryResponseRecord]]. */
object KinesisAnalyticsOutputDeliveryResponseRecord {

  /**
   * Encodes the record as a JSON object with the keys `recordId` and `result`.
   */
  implicit val encoder: JsonEncoder[KinesisAnalyticsOutputDeliveryResponseRecord] =
    DeriveJsonEncoder.gen[KinesisAnalyticsOutputDeliveryResponseRecord]

}

/**
 * The outcome of delivering one output record: `Ok` or `DeliveryFailed`.
 */
sealed trait KinesisAnalyticsOutputDeliveryResponseRecordResult

/** The cases of [[KinesisAnalyticsOutputDeliveryResponseRecordResult]] and its JSON encoder. */
object KinesisAnalyticsOutputDeliveryResponseRecordResult {

  /**
   * Encodes a result by its case name. On Scala 2 it writes an object keyed by the name, such as `{"Ok":{}}`. On
   * Scala 3 it writes the name as a string, such as `"Ok"`.
   */
  implicit val encoder: JsonEncoder[KinesisAnalyticsOutputDeliveryResponseRecordResult] =
    DeriveJsonEncoder.gen[KinesisAnalyticsOutputDeliveryResponseRecordResult]

  /** The record was delivered. */
  case object Ok extends KinesisAnalyticsOutputDeliveryResponseRecordResult

  /** The record could not be delivered. */
  case object DeliveryFailed extends KinesisAnalyticsOutputDeliveryResponseRecordResult
}
