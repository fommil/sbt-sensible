addCommandAlias("fmt", "all scalafmtSbt scalafmt")

organization := "com.fommil"
startYear := Some(2015)

name := "sbt-sensible"
sbtPlugin := true

crossSbtVersions := Seq("1.1.1", "0.13.17")

sonatypeGithost := (Gitlab, "fommil", "sbt-sensible")
licenses := Seq(Apache2)

addSbtPlugin("io.get-coursier"   % "sbt-coursier" % "1.0.2")
addSbtPlugin("com.jsuereth"      % "sbt-pgp"      % "1.1.1")
addSbtPlugin("com.dwijnand"      % "sbt-dynver"   % "2.1.0")
addSbtPlugin("org.xerial.sbt"    % "sbt-sonatype" % "2.3")
addSbtPlugin("de.heikoseeberger" % "sbt-header"   % "5.0.0")

scalafmtConfig in ThisBuild := Some(file("project/scalafmt.conf"))

scriptedBufferLog := false
scriptedLaunchOpts := Seq(
  "-Dplugin.version=" + version.value
)
