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

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")

val zioVersion     = "1.0.12"
val zioJsonVersion = "0.2.0-M2"
val sttpVersion    = "3.3.16"

lazy val root =
  project
    .in(file("."))
    .settings(publish / skip := true)
    .aggregate(zioLambda, zioLambdaExample)

lazy val zioLambda = module("zio-lambda", "lambda")
  .enablePlugins(BuildInfoPlugin)
  .settings(buildInfoSettings("zio.lambda"))
  .settings(
    stdSettings("zio-lambda"),
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %% "httpclient-backend" % sttpVersion
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
    assembly / assemblyJarName := "zio-lambda.jar"
  )
  .dependsOn(zioRuntime)
  .dependsOn(zioLambdaShared)

lazy val zioLambdaExample = module("zio-lambda-example", "lambda-example")
  .enablePlugins(NativeImagePlugin)
  .enablePlugins(BuildInfoPlugin)
  .settings(buildInfoSettings("zio.lambda.example"))
  .settings(
    stdSettings("zio-lambda-example"),
    assembly / assemblyJarName := "zio-lambda-example.jar",
    nativeImageOptions ++= List("--no-fallback", "--enable-http"),
    nativeImageOptions += s"-H:ReflectionConfigurationFiles=${target.value / "native-image-configs" / "reflect-config.json"}",
    nativeImageOptions += s"-H:ConfigurationFileDirectories=${target.value / "native-image-configs"}",
    nativeImageOptions += "-H:+JNI",
    nativeImageOptions += "-H:+AllowIncompleteClasspath"
  )
  .dependsOn(zioLambda)

lazy val zioRuntime = module("zio-runtime", "runtime")
  .enablePlugins(BuildInfoPlugin)
  .settings(buildInfoSettings("zio.runtime"))
  .settings(stdSettings("zio-runtime"))
  .settings(
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %% "httpclient-backend" % "3.3.15"
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
    assembly / assemblyJarName := "zio-runtime.jar",
    assembly / assemblyMergeStrategy := {
      case "META-INF/io.netty.versions.properties" =>
        MergeStrategy.concat
      case x => (assembly / assemblyMergeStrategy).value(x)
    }
  )
  .dependsOn(zioLambdaShared)
  .dependsOn(zioRuntimeLambda)

lazy val zioRuntimeLambda = module("zio-runtime-lambda", "runtime-lambda")
  .enablePlugins(BuildInfoPlugin)
  .settings(buildInfoSettings("zio.runtime.lambda"))
  .settings(stdSettings("zio-runtime"))
  .settings(testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")))
  .dependsOn(zioLambdaShared)

lazy val zioRuntimeLambdaExample = module("zio-runtime-lambda-example", "runtime-lambda-example")
  .enablePlugins(NativeImagePlugin)
  .enablePlugins(BuildInfoPlugin)
  .settings(buildInfoSettings("zio.runtime.lambda.example"))
  .settings(
    stdSettings("zio-runtime-lambda-example"),
    assembly / assemblyJarName := "zio-runtime-lambda-example.jar"
  )
  .dependsOn(zioRuntimeLambda)

lazy val zioLambdaShared = module("zio-lambda-shared", "shared")
  .enablePlugins(BuildInfoPlugin)
  .settings(buildInfoSettings("zio.lambda.shared"))
  .settings(stdSettings("zio-lambda-shared"))
  .settings(testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")))

def module(moduleName: String, fileName: String): Project =
  Project(moduleName, file(fileName))
    .settings(stdSettings(moduleName))
    .settings(
      assembly / assemblyExcludedJars := {
        val cp = (assembly / fullClasspath).value
        cp filter (_.data.getName.contains("scalaz-core"))
      },
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
