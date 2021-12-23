package zio.lambda.event

import zio.json._

final case class CloudFrontEvent(records: List[CloudFrontRecord])

object CloudFrontEvent {
  implicit val decoder: JsonDecoder[CloudFrontEvent] = DeriveJsonDecoder.gen[CloudFrontEvent]
}

final case class CloudFrontRecord(cf: CloudFrontRecordCF)
object CloudFrontRecord {
  implicit val decoder: JsonDecoder[CloudFrontRecord] = DeriveJsonDecoder.gen[CloudFrontRecord]
}

final case class CloudFrontRecordCF(
  config: CloudFrontRecordCFConfig,
  request: CloudFrontRecordCFRequest,
  response: CloudFrontRecordCFResponse
)
object CloudFrontRecordCF {
  implicit val decoder: JsonDecoder[CloudFrontRecordCF] = DeriveJsonDecoder.gen[CloudFrontRecordCF]
}

final case class CloudFrontRecordCFConfig(distributionId: String)
object CloudFrontRecordCFConfig {
  implicit val decoder: JsonDecoder[CloudFrontRecordCFConfig] = DeriveJsonDecoder.gen[CloudFrontRecordCFConfig]
}
final case class CloudFrontRecordCFRequest(
  uri: String,
  method: String,
  httpVersion: String,
  clientIp: String,
  headers: Map[String, List[CloudFrontRecordCFHttpHeader]]
)
object CloudFrontRecordCFRequest {
  implicit val decoder: JsonDecoder[CloudFrontRecordCFRequest] = DeriveJsonDecoder.gen[CloudFrontRecordCFRequest]
}

final case class CloudFrontRecordCFResponse(
  status: String,
  statusDescription: String,
  httpVersion: String,
  headers: Map[String, List[CloudFrontRecordCFHttpHeader]]
)
object CloudFrontRecordCFResponse {
  implicit val decoder: JsonDecoder[CloudFrontRecordCFResponse] = DeriveJsonDecoder.gen[CloudFrontRecordCFResponse]
}

final case class CloudFrontRecordCFHttpHeader(
  key: String,
  value: String
)
object CloudFrontRecordCFHttpHeader {
  implicit val decoder: JsonDecoder[CloudFrontRecordCFHttpHeader] =
    DeriveJsonDecoder.gen[CloudFrontRecordCFHttpHeader]
}
