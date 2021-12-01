package zio.lambda

import zio.json._

final case class ClientContext(
  client: ClientContext.Client,
  custom: Map[String, String],
  env: Map[String, String]
)

object ClientContext {
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
  implicit val decoder: JsonDecoder[ClientContext] = DeriveJsonDecoder.gen[ClientContext]
}
