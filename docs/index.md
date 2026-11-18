---
id: index
title: "Introduction to ZIO Lambda"
sidebar_label: "Introduction"
---

ZIO Lambda is a ZIO-based AWS Custom Runtime compatible with GraalVM Native Image. It lets you write an AWS Lambda function as a ZIO program, with typed JSON events in and typed JSON responses out, and deploy it as a GraalVM native binary.

@PROJECT_BADGES@

## Installation

ZIO Lambda is published for Scala 2.12, 2.13 and 3, and runs on the JVM, including as a GraalVM native image. Add the core module to your `build.sbt`:

```scala
libraryDependencies ++= Seq(
  // The runtime: ZLambdaRunner, Context. JVM only. It brings zio-json with it
  "dev.zio" %% "zio-lambda" % "@VERSION@"
)
```

## Modules

The event and response modules are optional. Add them when you want ready-made Scala types for AWS events and responses:

```scala
libraryDependencies ++= Seq(
  // Scala types for the events that AWS services send to Lambda. JVM only
  "dev.zio" %% "zio-lambda-event" % "@VERSION@",

  // Scala types for the responses that some AWS integrations expect. JVM only
  "dev.zio" %% "zio-lambda-response" % "@VERSION@"
)
```

See [Event and Response Types](./event-and-response-types.md) for what the two modules contain.

## Where to Go Next

1. [Getting Started](./getting-started.md) shows how to write a handler with custom event and response types.
2. The Reference section describes `ZLambdaRunner`, `Context` and every event and response type.
3. The tutorial builds an HTTP API on API Gateway step by step.
4. The how-to guides cover testing a handler locally and deploying it to AWS Lambda.
