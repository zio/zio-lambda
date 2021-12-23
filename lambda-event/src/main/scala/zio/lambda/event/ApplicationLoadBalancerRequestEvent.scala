package zio.lambda.event

import zio.json._

final case class ApplicationLoadBalancerRequestEvent(
  requestContext: ApplicationLoadBalancerRequestContext,
  httpMethod: String,
  path: String,
  queryStringParameters: Map[String, String],
  multiValueQueryStringParameters: Map[String, List[String]],
  headers: Map[String, String],
  multiValueHeaders: Map[String, List[String]],
  body: String,
  isBase64Encoded: Boolean
)

object ApplicationLoadBalancerRequestEvent {
  implicit val decoder: JsonDecoder[ApplicationLoadBalancerRequestEvent] =
    DeriveJsonDecoder.gen[ApplicationLoadBalancerRequestEvent]
}

final case class ApplicationLoadBalancerRequestContext(elb: ApplicationLoadBalancerRequestContextElb)
object ApplicationLoadBalancerRequestContext {
  implicit val decoder: JsonDecoder[ApplicationLoadBalancerRequestContext] =
    DeriveJsonDecoder.gen[ApplicationLoadBalancerRequestContext]
}

final case class ApplicationLoadBalancerRequestContextElb(targetGroupArn: String)
object ApplicationLoadBalancerRequestContextElb {
  implicit val decoder: JsonDecoder[ApplicationLoadBalancerRequestContextElb] =
    DeriveJsonDecoder.gen[ApplicationLoadBalancerRequestContextElb]
}
