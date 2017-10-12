organization := "com.fommil"

name := "sbt-sensible"
sbtPlugin := true

sonatypeGithub := ("fommil", "sbt-sensible")
licenses := Seq(Apache2)

addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC12")
addSbtPlugin("com.jsuereth"    % "sbt-pgp"      % "1.1.0")
addSbtPlugin("com.dwijnand"    % "sbt-dynver"   % "2.0.0")
addSbtPlugin("org.xerial.sbt"  % "sbt-sonatype" % "2.0")

scalafmtOnCompile in ThisBuild := true
scalafmtConfig in ThisBuild := file("project/scalafmt.conf")
scalafmtVersion in ThisBuild := "1.3.0"

scriptedSettings
scriptedBufferLog := false
scriptedLaunchOpts := Seq(
  "-Dplugin.version=" + version.value
)
