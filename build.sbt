import BuildHelper._

inThisBuild(
  List(
    organization := "dev.zio",
    homepage := Some(url("https://zio.github.io/zio-lambda/")),
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

val zioVersion         = "2.0.0-RC4"
val zioJsonVersion     = "0.3.0-RC4"
val awsLambdaJavaTests = "1.1.1"

lazy val root =
  project
    .in(file("."))
    .settings(publish / skip := true)
    .aggregate(
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
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-lambda-java-tests" % awsLambdaJavaTests % "test"
    )
  )
  .settings(
    topLevelDirectory := None,
    Universal / mappings ++= Seq(file("bootstrap") -> "bootstrap"),
    Compile / mainClass := Some("zio.lambda.internal.ZLambdaReflectiveApp")
  )

lazy val zioLambdaEvent = module("zio-lambda-event", "lambda-event")
  .enablePlugins(BuildInfoPlugin)
  .settings(buildInfoSettings("zio.lambda.event"))
  .settings(
    stdSettings("zio-lambda-event"),
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
    graalVMNativeImageOptions := Seq(
      "--verbose",
      "--no-fallback",
      "--install-exit-handlers",
      "--enable-http",
      "--allow-incomplete-classpath",
      "--report-unsupported-elements-at-runtime",
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
    publish / skip := true,
    moduleName := "zio-lambda-docs",
    scalacOptions -= "-Yno-imports",
    scalacOptions -= "-Xfatal-warnings",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion
    ),
    ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(root),
    ScalaUnidoc / unidoc / target := (LocalRootProject / baseDirectory).value / "website" / "static" / "api",
    cleanFiles += (ScalaUnidoc / unidoc / target).value,
    docusaurusCreateSite := docusaurusCreateSite.dependsOn(Compile / unidoc).value,
    docusaurusPublishGhpages := docusaurusPublishGhpages.dependsOn(Compile / unidoc).value
  )
  .dependsOn(zioLambda)
  .enablePlugins(MdocPlugin, DocusaurusPlugin, ScalaUnidocPlugin)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")
