package zio.lambda.event

import zio.json._

final case class CloudFormationCustomResourceEvent(
  requestType: String,
  serviceToken: String,
  responseUrl: String,
  stackId: String,
  requestId: String,
  logicalResourceId: String,
  physicalResourceId: String,
  resourceType: String,
  resourceProperties: Map[String, String],
  oldResourceProperties: Map[String, String]
)

object CloudFormationCustomResourceEvent {
  implicit val decoder: JsonDecoder[CloudFormationCustomResourceEvent] =
    DeriveJsonDecoder.gen[CloudFormationCustomResourceEvent]
}
