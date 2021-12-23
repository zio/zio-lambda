package zio.lambda.event

import zio.json._

final case class CloudWatchLogsEvent(awsLogs: CloudWatchLogsAWSLogs)

object CloudWatchLogsEvent {
  implicit val decoder: JsonDecoder[CloudWatchLogsEvent] = DeriveJsonDecoder.gen[CloudWatchLogsEvent]
}

final case class CloudWatchLogsAWSLogs(data: String)
object CloudWatchLogsAWSLogs {
  implicit val decoder: JsonDecoder[CloudWatchLogsAWSLogs] = DeriveJsonDecoder.gen[CloudWatchLogsAWSLogs]
}
