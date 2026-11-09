---
id: event-and-response-types
title: "Event and Response Types"
sidebar_label: "Event and Response Types"
---

The `zio-lambda-event` and `zio-lambda-response` modules provide Scala case classes that match the JSON that AWS services send to Lambda and expect back from it. Use them as the event and response types of your handler, so you do not have to write those case classes yourself.

Each type comes with its codec in its companion object, so `ZLambdaRunner.serve` finds it without an extra import. The tables below list the most common types. The complete set is in the [API documentation](https://javadoc.io/doc/dev.zio/zio-lambda-docs_2.13).

## Event Types

Event types live in the `zio.lambda.event` package. Each type decodes the JSON that the named AWS service sends:

| Type                                       | AWS source                                              |
| ------------------------------------------ | ------------------------------------------------------- |
| `APIGatewayProxyRequestEvent`              | API Gateway REST API with Lambda proxy integration      |
| `APIGatewayV2HttpEvent`                    | API Gateway HTTP API, payload format 2.0                |
| `APIGatewayV2WebSocketEvent`               | API Gateway WebSocket API                               |
| `ApplicationLoadBalancerRequestEvent`      | Application Load Balancer target group                  |
| `SQSEvent`                                 | Amazon SQS, a batch of messages                         |
| `KinesisEvent`                             | Amazon Kinesis Data Streams, a batch of records         |
| `KafkaEvent`                               | Amazon MSK or self-managed Apache Kafka                 |
| `ActiveMQEvent`                            | Amazon MQ for ActiveMQ                                  |
| `ScheduledEvent`                           | EventBridge scheduled rule                              |
| `CloudWatchLogsEvent`                      | CloudWatch Logs subscription filter                     |
| `CloudFormationCustomResourceEvent`        | CloudFormation custom resource                          |
| `ConnectEvent`                             | Amazon Connect contact flow                             |
| `CodeCommitEvent`                          | AWS CodeCommit repository trigger                       |
| `CloudFrontEvent`                          | CloudFront event for Lambda@Edge                        |

`APIGatewayProxyRequestEvent` is the exception to the companion rule: its codec is in the `APIGatewayProxyRequest` object in the same file, so import that object to use it with `ZLambdaRunner.serve`:

```scala mdoc:compile-only
import zio.lambda.event.APIGatewayProxyRequest._
```

## Response Types

Response types live in the `zio.lambda.response` package. Each type encodes the JSON that the named AWS integration expects:

| Type                                                   | AWS integration                                         |
| ------------------------------------------------------ | ------------------------------------------------------- |
| `APIGatewayProxyResponse`                              | API Gateway REST API with Lambda proxy integration      |
| `APIGatewayV2HTTPResponse`                             | API Gateway HTTP API, payload format 2.0                |
| `APIGatewayV2WebSocketResponse`                        | API Gateway WebSocket API                               |
| `ApplicationLoadBalancerResponse`                      | Application Load Balancer target group                  |
| `KinesisAnalyticsInputPreprocessingResponse`           | Kinesis Analytics input preprocessing Lambda            |
| `KinesisAnalyticsOutputDeliveryResponse`               | Kinesis Analytics output delivery Lambda                |

To use a response type, return it from your function. The example below pairs an HTTP API event with an HTTP API response:

```scala mdoc:compile-only
import zio._
import zio.lambda._
import zio.lambda.event._
import zio.lambda.response._

object HttpHandler extends ZIOAppDefault {

  val hello = (event: APIGatewayV2HttpEvent, _: Context) =>
    ZIO.succeed(APIGatewayV2HTTPResponse(body = s"You called ${event.rawPath}", statusCode = 200))

  override val run = ZLambdaRunner.serve(hello)
}
```

## Placeholder Types

Some types in the two modules are placeholders. They have no fields yet, so they decode any JSON object and discard its contents. Examples are `S3Event`, `SNSEvent`, `DynamoDBEvent`, `S3BatchEvent` and `S3BatchResponse`. Do not rely on them to read data from an event. Define your own case class for the fields you need, with its own codec, until the type is filled in.

## Custom Types

If the AWS event you receive is not in the modules, or you need only some of its fields, write your own case class and codec, as shown in [Getting Started](./getting-started.md). zio-json ignores JSON fields that your case class does not declare, so you can decode a subset of a larger event.
