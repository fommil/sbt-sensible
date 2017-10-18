organization := "com.fommil"

name := "sbt-sensible"
sbtPlugin := true
crossSbtVersions := Seq("0.13.16", "1.0.2")

// WORKAROUND: https://github.com/sbt/sbt/issues/3636
// fixed in 0.13.17
def addSbtPlugin1(dependency: ModuleID): Setting[Seq[ModuleID]] =
  libraryDependencies += {
    val sbtV   = (sbtBinaryVersion in pluginCrossBuild).value
    val scalaV = (scalaBinaryVersion in update).value
    Defaults.sbtPluginExtra(dependency, sbtV, scalaV)
  }

// use a local.sbt with the `sbtVersion in Global` set appropriately

sonatypeGithost := ("gitlab.com", "fommil", "sbt-sensible")
licenses := Seq(Apache2)

addSbtPlugin1("io.get-coursier"   % "sbt-coursier" % "1.0.0-RC12")
addSbtPlugin1("com.jsuereth"      % "sbt-pgp"      % "1.1.0")
addSbtPlugin1("com.dwijnand"      % "sbt-dynver"   % "2.0.0")
addSbtPlugin1("org.xerial.sbt"    % "sbt-sonatype" % "2.0")
addSbtPlugin1("de.heikoseeberger" % "sbt-header"   % "3.0.2")

scalafmtOnCompile in ThisBuild := true
scalafmtConfig in ThisBuild := file("project/scalafmt.conf")
scalafmtVersion in ThisBuild := "1.3.0"

scriptedSettings
scriptedBufferLog := false
scriptedLaunchOpts := Seq(
  "-Dplugin.version=" + version.value
)
