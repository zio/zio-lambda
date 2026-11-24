package zio.lambda.event

import zio.json._

/** An AWS CloudFormation custom resource request delivered to a Lambda function. */
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

  /** Decodes a [[CloudFormationCustomResourceEvent]] from JSON. */
  implicit val decoder: JsonDecoder[CloudFormationCustomResourceEvent] =
    DeriveJsonDecoder.gen[CloudFormationCustomResourceEvent]
}
