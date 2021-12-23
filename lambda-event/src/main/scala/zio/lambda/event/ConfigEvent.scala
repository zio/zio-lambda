package zio.lambda.event

import zio.json._

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

object Config {
  implicit val decoder: JsonDecoder[ConfigEvent] = DeriveJsonDecoder.gen[ConfigEvent]
}
