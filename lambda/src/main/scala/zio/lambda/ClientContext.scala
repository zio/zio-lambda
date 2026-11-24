package zio.lambda

import zio.json._

/**
 * Data about the client application that invoked the function through the AWS Mobile SDK. The runner puts it in
 * [[Context]] as `clientContext`.
 *
 * @param client the client application
 * @param custom custom values set by the client application
 * @param env environment values reported by the AWS Mobile SDK
 */
final case class ClientContext(
  client: Client,
  custom: Map[String, String],
  env: Map[String, String]
)

/** Holds the JSON decoder of [[ClientContext]]. */
object ClientContext {

  /** Decodes a JSON object with the keys `client`, `custom` and `env`, named as the fields. */
  implicit val decoder: JsonDecoder[ClientContext] = DeriveJsonDecoder.gen[ClientContext]
}

/**
 * The client application in a [[ClientContext]].
 *
 * @param installationId the ID of this installation of the application
 * @param appTitle the title of the application
 * @param appVersionName the version name of the application
 * @param appVersionCode the version code of the application
 * @param appPackageName the package name of the application
 */
final case class Client(
  installationId: String,
  appTitle: String,
  appVersionName: String,
  appVersionCode: String,
  appPackageName: String
)

/** Holds the JSON decoder of [[Client]]. */
object Client {

  /** Decodes a JSON object whose keys are the field names, such as `installationId` and `appTitle`. */
  implicit val decoder: JsonDecoder[Client] = DeriveJsonDecoder.gen[Client]
}
