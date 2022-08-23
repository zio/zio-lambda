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
import zio.lambda.event._

object SimpleHandler extends ZLambda[KinesisEvent, String] {

  override def apply(event: KinesisEvent, context: Context): Task[String] =
    for {
      _ <- printLine(event)
    } yield "Handler ran successfully"
}
```
zio-lambda depends on [**zio-json**](https://github.com/zio/zio-json) for decoding any event you send to it and enconding any response you send back to the Lambda service. 
You can either create your own data types or use the ones that are included in **zio-lambda-event** and **zio-lambda-response**.

The last step is to define the way your function will be invoked. There are three ways, detailed below:

## Lambda layer
Upload zio-lambda as a [Lambda Layer](https://docs.aws.amazon.com/lambda/latest/dg/configuration-layers.html) 
Each release will contain a zip file ready to be used as a lambda layer) and your function. Instructions coming soon!

## Direct deployment of native image binary

1. Create an AWS Lambda function and choose the runtime where you provide your own bootstrap on Amazon Linux 2
  
    ![create-lambda](https://user-images.githubusercontent.com/14280155/164102664-3686e415-20be-4dd9-8979-ea6098a7a4b9.png)
2. Run `sbt graalvm-native-image:packageBin`, we'll find the binary present under the `graalvm-native-image` folder:

    ![binary-located](https://user-images.githubusercontent.com/14280155/164103337-6645dfeb-7fc4-4f7f-9b13-8005b0cddead.png)

3. Create the following bootstap file (which calls out to the binary) and place it in the same directory alongside the binary:
    ```bash
    #!/usr/bin/env bash
    
    set -euo pipefail
    
    ./zio-lambda-example
    ```

    ![bootstrap-alongside-native-binary](https://user-images.githubusercontent.com/14280155/164103935-0bf7a6cb-814d-4de1-8fa1-4d0d54fb6e88.png)


4. Now we can zip both these files up:
    ```log
    > pwd
    /home/cal/IdeaProjects/zio-lambda/lambda-example/target/graalvm-native-image                                                                                                                                
    > zip upload.zip bootstrap zio-lambda-example
    ```

5. Take `upload.zip` and upload it to AWS Lambda and test your function:

    ![lambda-ui](https://user-images.githubusercontent.com/14280155/164104747-039ec584-d3e2-4b47-884d-ff88977e2b53.png)

6. Test everything out to make sure everything works:

    ![test-ui](https://user-images.githubusercontent.com/14280155/164104858-a720ac55-b9bb-47ec-af70-c4bd5eb5bed3.png)

## Deployment of native image binary in a Docker container

Following the steps from `Direct deployment of native image binary` to produce your native image binary, we can package
up the native binary into a Docker image and deploy it like that to AWS Lambda.

```Dockerfile
FROM gcr.io/distroless/base
COPY lambda-example/target/graalvm-native-image/zio-lambda-example /app/zio-lambda-example
CMD ["/app/zio-lambda-example"]
```

**NOTE:** This Dockerfile is meant to build the lambda-example located in the zio-lambda project and the Dockerfile is 
placed in the zio-lambda-repository. You will need to adjust this Dockerfile to match your project needs.

Now we can build and tag the Docker image:
```shell
docker build -t native-image-binary .
```

Take this image and push it to AWS ECR:
```bash
pass=$(aws ecr get-login-password --region us-east-1) 
docker login --username AWS --password $pass <your_AWS_ECR_REPO>   
docker tag native-image-binary <your-particular-ecr-image-repository>:<your-tag>
docker push <your-particular-ecr-image-repository>:<your-tag>
```

Here is an example:

![image-uploaded](https://user-images.githubusercontent.com/14280155/164120591-68a78d19-c56b-4793-96b8-cfe567443063.png)

Create a Lambda function and choose container image:

![lambda-create-container-image](https://user-images.githubusercontent.com/14280155/164120637-9c827736-26a8-4c65-92d4-2919157bbda6.png)

![image](https://user-images.githubusercontent.com/14280155/164120764-2c736a46-29e3-488c-ba6a-e2b69ef51792.png)

Please note that because you incur the overhead of your native binary residing within a Docker container, there is more overhead than the other approach of deploying the binary straight to AWS Lambda
