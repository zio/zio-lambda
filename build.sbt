import BuildHelper.*

inThisBuild(
  List(
    organization := "dev.zio",
    homepage := Some(url("https://zio.dev/zio-lambda/")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "jrmsamson",
        "Jerome Samson",
        "jeromess.88@gmail.com",
        url("https://github.com/jrmsamson")
      ),
      Developer(
        "jdegoes",
        "John De Goes",
        "john@degoes.net",
        url("http://degoes.net")
      )
    ),
    pgpPassphrase := sys.env.get("PGP_PASSWORD").map(_.toArray),
    pgpPublicRing := file("/tmp/public.asc"),
    pgpSecretRing := file("/tmp/secret.asc"),
    scmInfo := Some(
      ScmInfo(url("https://github.com/zio/zio-lambda/"), "scm:git:git@github.com:zio/zio-lambda.git")
    )
  )
)

val zioVersion         = "2.1.24"
val zioJsonVersion     = "0.7.3"
val awsLambdaJavaTests = "1.1.1"

lazy val root =
  project
    .in(file("."))
    .settings(publish / skip := true)
    .aggregate(
      docs,
      zioLambda,
      zioLambdaExample,
      zioLambdaEvent,
      zioLambdaResponse
    )

lazy val zioLambda = module("zio-lambda", "lambda")
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(JavaAppPackaging)
  .settings(buildInfoSettings("zio.lambda"))
  .settings(
    stdSettings("zio-lambda"),
    scalacOptions -= "-Xfatal-warnings", //temporary disable fatal errors on depricated method calls.
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-lambda-java-tests" % awsLambdaJavaTests % "test"
    )
  )
  .settings(
    topLevelDirectory := None,
    Universal / mappings ++= Seq(file("bootstrap") -> "bootstrap"),
    Compile / mainClass := Some("zio.lambda.internal.ZLambdaAppReflective")
  )

lazy val zioLambdaEvent = module("zio-lambda-event", "lambda-event")
  .enablePlugins(BuildInfoPlugin)
  .settings(buildInfoSettings("zio.lambda.event"))
  .settings(
    stdSettings("zio-lambda-event"),
    scalacOptions -= "-Yretain-trees",
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-lambda-java-tests" % awsLambdaJavaTests % "test"
    )
  )

lazy val zioLambdaResponse = module("zio-lambda-response", "lambda-response")
  .enablePlugins(BuildInfoPlugin)
  .settings(buildInfoSettings("zio.lambda.response"))
  .settings(
    stdSettings("zio-lambda-response"),
    scalacOptions -= "-Yretain-trees",
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-lambda-java-tests" % awsLambdaJavaTests % "test"
    )
  )

lazy val zioLambdaExample = module("zio-lambda-example", "lambda-example")
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(GraalVMNativeImagePlugin)
  .settings(buildInfoSettings("zio.lambda.example"))
  .settings(
    publish / skip := true,
    name := "zio-lambda-example",
    stdSettings("zio-lambda-example"),
    assembly / assemblyJarName := "zio-lambda-example.jar",
    GraalVMNativeImage / mainClass := Some("zio.lambda.example.SimpleHandler"),
    GraalVMNativeImage / containerBuildImage := Some("ghcr.io/graalvm/native-image-community:21.0.2"),
    graalVMNativeImageOptions := Seq(
      "--verbose",
      "--no-fallback",
      "--install-exit-handlers",
      "--enable-http",
      "--link-at-build-time",
      "--report-unsupported-elements-at-runtime",
      "-H:+UnlockExperimentalVMOptions",
      "-H:+StaticExecutableWithDynamicLibC",
      "-H:+RemoveSaturatedTypeFlows"
    )
  )
  .dependsOn(zioLambda)

def module(moduleName: String, fileName: String): Project =
  Project(moduleName, file(fileName))
    .settings(stdSettings(moduleName))
    .settings(
      libraryDependencies ++= Seq(
        "dev.zio" %% "zio"          % zioVersion,
        "dev.zio" %% "zio-test"     % zioVersion % "test",
        "dev.zio" %% "zio-test-sbt" % zioVersion % "test",
        "dev.zio" %% "zio-json"     % zioJsonVersion
      )
    )

lazy val docs = project
  .in(file("zio-lambda-docs"))
  .settings(
    moduleName := "zio-lambda-docs",
    projectName := "ZIO Lambda",
    mainModuleName := (zioLambda / moduleName).value,
    projectStage := ProjectStage.Development,
    ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(zioLambda, zioLambdaEvent, zioLambdaResponse),
    docsPublishBranch := "master",
    excludeDependencies += "org.scala-lang.modules" % "scala-collection-compat_2.13"
  )
  .dependsOn(zioLambda, zioLambdaEvent, zioLambdaResponse)
  .enablePlugins(WebsitePlugin)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")
