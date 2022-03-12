# ZIO Lambda

| Project Stage | CI | Release | Snapshot | Discord |
| --- | --- | --- | --- | --- |
| [![Project stage][Stage]][Stage-Page] | ![CI][Badge-CI] | [![Release Artifacts][Badge-SonatypeReleases]][Link-SonatypeReleases] | [![Snapshot Artifacts][Badge-SonatypeSnapshots]][Link-SonatypeSnapshots] | [![Badge-Discord]][Link-Discord] |

[Stage]: https://img.shields.io/badge/Project%20Stage-Development-yellowgreen.svg
[Stage-Page]: https://github.com/zio/zio/wiki/Project-Stages
[Badge-SonatypeReleases]: https://img.shields.io/nexus/r/https/oss.sonatype.org/dev.zio/zio-lambda_2.12.svg "Sonatype Releases"
[Badge-SonatypeSnapshots]: https://img.shields.io/nexus/s/https/oss.sonatype.org/dev.zio/zio-lambda_2.12.svg "Sonatype Snapshots"
[Badge-CI]: https://github.com/zio/zio-lambda/workflows/CI/badge.svg
[Link-SonatypeReleases]: https://oss.sonatype.org/content/repositories/releases/dev/zio/zio-lambda_2.12/ "Sonatype Releases"
[Link-SonatypeSnapshots]: https://oss.sonatype.org/content/repositories/snapshots/dev/zio/zio-lambda_2.12/ "Sonatype Snapshots"
[Badge-Discord]: https://img.shields.io/discord/629491597070827530?logo=discord "chat on discord"
[Link-Discord]: https://discord.gg/a37AwDkyvC "Discord"

# Overview

A ZIO-based AWS Custom Runtime compatible with GraalVM Native Image.

# Installation


```scala
libraryDependencies += "dev.zio" %% "zio-lambda" % "1.0.0-RC1"
```

### Optional dependencies
```scala
libraryDependencies += "dev.zio" %% "zio-lambda-event"    % "1.0.0-RC1"

libraryDependencies += "dev.zio" %% "zio-lambda-response" % "1.0.0-RC1"
```

# Usage

Create your Lambda function by extending ZLambda

```scala
import zio.Console._
import zio._
import zio.lambda._

object SimpleHandler extends ZLambda[KinesisEvent, String] {

  override def apply(event: KinesisEvent, context: Context): RIO[ZEnv, String] =
    for {
      _ <- printLine(event.message)
    } yield "Handler ran successfully"
}
```
zio-lambda depends on [**zio-json**](https://github.com/zio/zio-json) for decoding any event you send to it and enconding any response you send back to the Lambda service. 
You can either create your own data types or use the ones that are included in **zio-lambda-event** and **zio-lambda-response**.

The last step is to define the way your function will be invoked. There are two ways:

* Upload zio-lambda as a [Lambda Layer](https://docs.aws.amazon.com/lambda/latest/dg/configuration-layers.html) (Each release will contain a zip file ready to be used as a lambda layer) and your function.
  
* Include zio-lambda in your function's deployment package in the form of an executable file named **bootstrap** by using [GraalVM Native Image](https://www.graalvm.org/22.0/reference-manual/native-image/)
