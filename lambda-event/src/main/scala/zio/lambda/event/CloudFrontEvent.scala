package zio.lambda.event

import zio.json._

/** An Amazon CloudFront event delivered to a Lambda@Edge function. */
final case class CloudFrontEvent(records: List[CloudFrontRecord])

object CloudFrontEvent {

  /** Decodes a [[CloudFrontEvent]] from JSON. */
  implicit val decoder: JsonDecoder[CloudFrontEvent] = DeriveJsonDecoder.gen[CloudFrontEvent]
}

/** One record of a [[CloudFrontEvent]]. */
final case class CloudFrontRecord(cf: CloudFrontRecordCF)
object CloudFrontRecord {

  /** Decodes a [[CloudFrontRecord]] from JSON. */
  implicit val decoder: JsonDecoder[CloudFrontRecord] = DeriveJsonDecoder.gen[CloudFrontRecord]
}

/** The `cf` object of a [[CloudFrontRecord]]: the distribution configuration, the request and the response. */
final case class CloudFrontRecordCF(
  config: CloudFrontRecordCFConfig,
  request: CloudFrontRecordCFRequest,
  response: CloudFrontRecordCFResponse
)
object CloudFrontRecordCF {

  /** Decodes a [[CloudFrontRecordCF]] from JSON. */
  implicit val decoder: JsonDecoder[CloudFrontRecordCF] = DeriveJsonDecoder.gen[CloudFrontRecordCF]
}

/** The CloudFront distribution configuration of a [[CloudFrontRecordCF]]. */
final case class CloudFrontRecordCFConfig(distributionId: String)
object CloudFrontRecordCFConfig {

  /** Decodes a [[CloudFrontRecordCFConfig]] from JSON. */
  implicit val decoder: JsonDecoder[CloudFrontRecordCFConfig] = DeriveJsonDecoder.gen[CloudFrontRecordCFConfig]
}

/** The viewer or origin request of a [[CloudFrontRecordCF]]. */
final case class CloudFrontRecordCFRequest(
  uri: String,
  method: String,
  httpVersion: String,
  clientIp: String,
  headers: Map[String, List[CloudFrontRecordCFHttpHeader]]
)
object CloudFrontRecordCFRequest {

  /** Decodes a [[CloudFrontRecordCFRequest]] from JSON. */
  implicit val decoder: JsonDecoder[CloudFrontRecordCFRequest] = DeriveJsonDecoder.gen[CloudFrontRecordCFRequest]
}

/** The response of a [[CloudFrontRecordCF]]. */
final case class CloudFrontRecordCFResponse(
  status: String,
  statusDescription: String,
  httpVersion: String,
  headers: Map[String, List[CloudFrontRecordCFHttpHeader]]
)
object CloudFrontRecordCFResponse {

  /** Decodes a [[CloudFrontRecordCFResponse]] from JSON. */
  implicit val decoder: JsonDecoder[CloudFrontRecordCFResponse] = DeriveJsonDecoder.gen[CloudFrontRecordCFResponse]
}

/** One HTTP header entry, as a key and a value, of a CloudFront request or response. */
final case class CloudFrontRecordCFHttpHeader(
  key: String,
  value: String
)
object CloudFrontRecordCFHttpHeader {

  /** Decodes a [[CloudFrontRecordCFHttpHeader]] from JSON. */
  implicit val decoder: JsonDecoder[CloudFrontRecordCFHttpHeader] =
    DeriveJsonDecoder.gen[CloudFrontRecordCFHttpHeader]
}
