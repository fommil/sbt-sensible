organization := "com.fommil"
startYear := Some(2015)

name := "sbt-sensible"
sbtPlugin := true
crossSbtVersions := Seq("0.13.16", "1.0.3")

// WORKAROUND: https://github.com/sbt/sbt/issues/3636
// fixed in 0.13.17
def addSbtPlugin1(dependency: ModuleID): Setting[Seq[ModuleID]] =
  libraryDependencies += {
    val sbtV   = (sbtBinaryVersion in pluginCrossBuild).value
    val scalaV = (scalaBinaryVersion in update).value
    Defaults.sbtPluginExtra(dependency, sbtV, scalaV)
  }

// use a local.sbt with the `sbtVersion in Global` set appropriately

sonatypeGithost := (Gitlab, "fommil", "sbt-sensible")
licenses := Seq(Apache2)

addSbtPlugin1("io.get-coursier"   % "sbt-coursier" % "1.0.0-RC13")
addSbtPlugin1("com.jsuereth"      % "sbt-pgp"      % "1.1.0")
addSbtPlugin1("com.dwijnand"      % "sbt-dynver"   % "2.0.0")
addSbtPlugin1("org.xerial.sbt"    % "sbt-sonatype" % "2.0")
addSbtPlugin1("de.heikoseeberger" % "sbt-header"   % "4.0.0")

scalafmtOnCompile in ThisBuild := true
scalafmtConfig in ThisBuild := file("project/scalafmt.conf")
scalafmtVersion in ThisBuild := "1.3.0"

scriptedSettings
scriptedBufferLog := false
scriptedLaunchOpts := Seq(
  "-Dplugin.version=" + version.value
)
