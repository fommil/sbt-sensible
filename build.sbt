addCommandAlias("fmt", "all scalafmtSbt scalafmt")

organization := "com.fommil"
startYear := Some(2015)

name := "sbt-sensible"
sbtPlugin := true

crossSbtVersions := Seq(sbtVersion.value, "0.13.17")

sonatypeGithost := (Gitlab, "fommil", name.value)
licenses := Seq(LGPL3)
sonatypeDevelopers := List("Sam Halliday")

addSbtPlugin("io.get-coursier"   % "sbt-coursier" % "1.0.3")
addSbtPlugin("com.jsuereth"      % "sbt-pgp"      % "1.1.1")
addSbtPlugin("com.dwijnand"      % "sbt-dynver"   % "3.0.0")
addSbtPlugin("org.xerial.sbt"    % "sbt-sonatype" % "2.3")
addSbtPlugin("de.heikoseeberger" % "sbt-header"   % "5.0.0")

scalafmtConfig in ThisBuild := Some(file("project/scalafmt.conf"))

scriptedBufferLog := false
scriptedLaunchOpts := Seq(
  "-Dplugin.version=" + version.value
)
