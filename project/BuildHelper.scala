import sbt._
import sbt.Keys._
import sbtbuildinfo._
import BuildInfoKeys._

object BuildHelper {
  private val Scala212             = "2.12.16"
  private val Scala213             = "2.13.11"
  private val Scala3               = "3.2.0"
  private val KindProjectorVersion = "0.13.2"

  def buildInfoSettings(packageName: String) =
    Seq(
      buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, isSnapshot),
      buildInfoPackage := packageName,
      buildInfoObject := "BuildInfo"
    )

  def kindProjector(scalaVer: String) =
    if (scalaVer != Scala3)
      Seq(
        compilerPlugin("org.typelevel" %% "kind-projector" % KindProjectorVersion cross CrossVersion.full)
      )
    else
      Seq.empty

  def stdSettings(prjName: String) =
    Seq(
      name := s"$prjName",
      crossScalaVersions := Seq(Scala212, Scala213, Scala3),
      ThisBuild / scalaVersion := Scala213,
      libraryDependencies ++= kindProjector(scalaVersion.value),
      incOptions ~= (_.withLogRecompileOnMacro(false))
    )
}
