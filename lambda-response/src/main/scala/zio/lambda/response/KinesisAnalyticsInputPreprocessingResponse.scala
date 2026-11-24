package zio.lambda.response

import zio.json._

/**
 * The response that a Kinesis Data Analytics input preprocessing function returns: one result per input record,
 * matched by `recordId`. Return it from a handler that receives a
 * `zio.lambda.event.KinesisAnalyticsFirehoseInputPreprocessingEvent` or a
 * `zio.lambda.event.KinesisAnalyticsStreamsInputPreprocessingEvent`.
 *
 * The encoding of the record results differs between Scala versions. On Scala 2 each result is written as an object
 * keyed by the case name, `{"Ok":{}}`, and on Scala 3 as the case name in a string, `"Ok"`. Kinesis Data Analytics
 * expects the string form.
 *
 * {{{
 * import KinesisAnalyticsInputPreprocessingResponseRecordResult._
 *
 * val response = KinesisAnalyticsInputPreprocessingResponse(
 *   List(
 *     KinesisAnalyticsInputPreprocessingResponseRecord("record-1", Ok),
 *     KinesisAnalyticsInputPreprocessingResponseRecord("record-2", Dropped)
 *   )
 * )
 * }}}
 *
 * @param records the result for each input record
 *
 * @see [[https://zio.dev/zio-lambda/ ZIO Lambda]]
 */
final case class KinesisAnalyticsInputPreprocessingResponse(
  records: List[KinesisAnalyticsInputPreprocessingResponseRecord]
)

/** Holds the JSON encoder of [[KinesisAnalyticsInputPreprocessingResponse]]. */
object KinesisAnalyticsInputPreprocessingResponse {

  /**
   * Encodes the response as a JSON object with a `records` array.
   */
  implicit val encoder: JsonEncoder[KinesisAnalyticsInputPreprocessingResponse] =
    DeriveJsonEncoder.gen[KinesisAnalyticsInputPreprocessingResponse]

}

/**
 * The result of preprocessing one input record.
 *
 * @param recordId the ID of the input record that this result answers
 * @param result whether the record was processed, dropped or failed
 */
final case class KinesisAnalyticsInputPreprocessingResponseRecord(
  recordId: String,
  result: KinesisAnalyticsInputPreprocessingResponseRecordResult
)

/** Holds the JSON encoder of [[KinesisAnalyticsInputPreprocessingResponseRecord]]. */
object KinesisAnalyticsInputPreprocessingResponseRecord {

  /**
   * Encodes the record as a JSON object with the keys `recordId` and `result`.
   */
  implicit val encoder: JsonEncoder[KinesisAnalyticsInputPreprocessingResponseRecord] =
    DeriveJsonEncoder.gen[KinesisAnalyticsInputPreprocessingResponseRecord]

}

/**
 * The outcome of preprocessing one input record: `Ok`, `ProcessingFailed` or `Dropped`.
 */
sealed trait KinesisAnalyticsInputPreprocessingResponseRecordResult

/** The cases of [[KinesisAnalyticsInputPreprocessingResponseRecordResult]] and its JSON encoder. */
object KinesisAnalyticsInputPreprocessingResponseRecordResult {

  /**
   * Encodes a result by its case name. On Scala 2 it writes an object keyed by the name, such as `{"Ok":{}}`. On
   * Scala 3 it writes the name as a string, such as `"Ok"`.
   */
  implicit val encoder: JsonEncoder[KinesisAnalyticsInputPreprocessingResponseRecordResult] =
    DeriveJsonEncoder.gen[KinesisAnalyticsInputPreprocessingResponseRecordResult]

  /** The record was processed. */
  case object Ok extends KinesisAnalyticsInputPreprocessingResponseRecordResult

  /** The record could not be processed. */
  case object ProcessingFailed extends KinesisAnalyticsInputPreprocessingResponseRecordResult

  /** The record is dropped on purpose. */
  case object Dropped extends KinesisAnalyticsInputPreprocessingResponseRecordResult
}
