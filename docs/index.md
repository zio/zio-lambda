---
id: index
title: "Introduction to ZIO Lambda"
sidebar_label: "ZIO Lambda"
---

ZIO Lambda is a ZIO-based AWS Custom Runtime compatible with GraalVM Native Image. It lets you write an AWS Lambda function as a ZIO program, with typed JSON events in and typed JSON responses out, and run it as a native binary or as a JVM layer.

@PROJECT_BADGES@

## Installation

To add ZIO Lambda to an sbt project, declare the core module and, optionally, the event and response modules:

```sbt
libraryDependencies += "dev.zio" %% "zio-lambda" % "@VERSION@"

// Optional dependencies
libraryDependencies += "dev.zio" %% "zio-lambda-event"    % "@VERSION@"
libraryDependencies += "dev.zio" %% "zio-lambda-response" % "@VERSION@"
```

The core module depends on [zio-json](https://github.com/zio/zio-json), so you do not need to declare it yourself. ZIO Lambda uses zio-json to decode the incoming event and to encode the response.

The `zio-lambda-event` module contains Scala types for the events that AWS services send to Lambda, such as API Gateway requests and SQS batches. The `zio-lambda-response` module contains Scala types for the responses that some AWS integrations expect, such as the API Gateway proxy response.

## Where to Go Next

The guides in this section follow the order in which you would build a function:

1. [Getting Started](./getting-started.md) shows how to write a handler with custom event and response types.
2. [Event and Response Types](./event-and-response-types.md) lists the AWS event and response types that the optional modules provide.
3. [Testing Locally](./testing-locally.md) shows how to call a handler from a test without deploying it.
4. [Deployment](./deployment.md) explains how to package a native binary and deploy it to AWS Lambda, directly or in a container image.
