package zio.lambda

import zio.json._

final case class ClientContext(
  client: Client,
  custom: Map[String, String],
  env: Map[String, String]
)

object ClientContext {
  implicit val decoder: JsonDecoder[ClientContext] = DeriveJsonDecoder.gen[ClientContext]
}

final case class Client(
  installationId: String,
  appTitle: String,
  appVersionName: String,
  appVersionCode: String,
  appPackageName: String
)

object Client {
  implicit val decoder: JsonDecoder[Client] = DeriveJsonDecoder.gen[Client]
}
