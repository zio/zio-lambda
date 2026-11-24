package zio.lambda.event

import zio.json._

/** An Application Load Balancer request delivered to a Lambda function target. */
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

  /** Decodes a [[ApplicationLoadBalancerRequestEvent]] from JSON. */
  implicit val decoder: JsonDecoder[ApplicationLoadBalancerRequestEvent] =
    DeriveJsonDecoder.gen[ApplicationLoadBalancerRequestEvent]
}

/** The `requestContext` of an [[ApplicationLoadBalancerRequestEvent]]. */
final case class ApplicationLoadBalancerRequestContext(elb: ApplicationLoadBalancerRequestContextElb)
object ApplicationLoadBalancerRequestContext {

  /** Decodes a [[ApplicationLoadBalancerRequestContext]] from JSON. */
  implicit val decoder: JsonDecoder[ApplicationLoadBalancerRequestContext] =
    DeriveJsonDecoder.gen[ApplicationLoadBalancerRequestContext]
}

/** The `elb` object of an [[ApplicationLoadBalancerRequestContext]]. */
final case class ApplicationLoadBalancerRequestContextElb(targetGroupArn: String)
object ApplicationLoadBalancerRequestContextElb {

  /** Decodes a [[ApplicationLoadBalancerRequestContextElb]] from JSON. */
  implicit val decoder: JsonDecoder[ApplicationLoadBalancerRequestContextElb] =
    DeriveJsonDecoder.gen[ApplicationLoadBalancerRequestContextElb]
}
