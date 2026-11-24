package zio.lambda.event

import zio.json._

/** An AWS Config custom rule invocation event delivered to a Lambda function. */
final case class ConfigEvent(
  version: String,
  invokingEvent: String,
  ruleParameters: String,
  resultToken: String,
  configRuleArn: String,
  configRuleId: String,
  configRuleName: String,
  accountId: String,
  executionRoleArn: String,
  eventLeftScope: Boolean
)

/** Holds the JSON decoder for [[ConfigEvent]]. */
object Config {

  /** Decodes a [[ConfigEvent]] from JSON. */
  implicit val decoder: JsonDecoder[ConfigEvent] = DeriveJsonDecoder.gen[ConfigEvent]
}
