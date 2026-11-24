package zio.lambda.event

import zio.json._

/** An Amazon CloudWatch Logs subscription filter payload delivered to a Lambda function. */
final case class CloudWatchLogsEvent(awsLogs: CloudWatchLogsAWSLogs)

object CloudWatchLogsEvent {

  /** Decodes a [[CloudWatchLogsEvent]] from JSON. */
  implicit val decoder: JsonDecoder[CloudWatchLogsEvent] = DeriveJsonDecoder.gen[CloudWatchLogsEvent]
}

/** The log payload of a [[CloudWatchLogsEvent]]; `data` holds the log data as a string, exactly as it arrives. */
final case class CloudWatchLogsAWSLogs(data: String)
object CloudWatchLogsAWSLogs {

  /** Decodes a [[CloudWatchLogsAWSLogs]] from JSON. */
  implicit val decoder: JsonDecoder[CloudWatchLogsAWSLogs] = DeriveJsonDecoder.gen[CloudWatchLogsAWSLogs]
}
