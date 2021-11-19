package zio.lambda

import zio.json._

sealed trait LambdaResponse

object LambdaResponse {
  final case class APIGatewayProxy(
    statusCode: Int,
    headers: Map[String, String],
    multiValueHeaders: Map[String, List[String]],
    body: String,
    isBase64Encoded: Boolean
  )

  object APIGatewayProxy {
    implicit val decoder: JsonDecoder[APIGatewayProxy] = DeriveJsonDecoder.gen[APIGatewayProxy]
  }

  /**
   * API Gateway v2 event: https://docs.aws.amazon.com/lambda/latest/dg/services-apigateway.html
   */
  final case class APIGatewayV2HTTP(
    statusCode: Int,
    headers: Map[String, String],
    multiValueHeaders: Map[String, List[String]],
    cookies: List[String],
    body: String,
    isBase64Encoded: Boolean
  )

  final case class APIGatewayV2WebSocket(
    isBase64Encoded: Boolean,
    statusCode: Int,
    headers: Map[String, String],
    multiValueHeaders: Map[String, List[String]],
    body: String
  )

  final case class ApplicationLoadBalancer(
    statusCode: Int,
    statusDescription: String,
    isBase64Encoded: Boolean,
    headers: Map[String, String],
    multiValueHeaders: Map[String, List[String]],
    body: String
  )

  final case class KinesisAnalyticsInputPreprocessing(
    records: List[KinesisAnalyticsInputPreprocessing.Record]
  )

  object KinesisAnalyticsInputPreprocessing {
    final case class Record(recordId: String, result: Result)
    sealed trait Result
    object Result {
      case object Ok               extends Result
      case object ProcessingFailed extends Result
      case object Dropped          extends Result
    }
  }

  final case class KinesisAnalyticsOutputDelivery(records: List[KinesisAnalyticsOutputDelivery.Record])

  object KinesisAnalyticsOutputDelivery {
    final case class Record(recordId: String, result: Result)
    sealed trait Result
    object Result {
      case object Ok             extends Result
      case object DeliveryFailed extends Result
    }
  }

  final case class S3Batch()

  final case class SimpleIAMPolicy()

}
