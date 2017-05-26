organization := "com.fommil"

name := "sbt-sensible"
sbtPlugin := true

sonatypeGithub := ("fommil", "sbt-sensible")
licenses := Seq(Apache2)

// intentionally not depending on the plugin, to allow scalafmt
libraryDependencies += "org.scalariform" %% "scalariform" % "0.1.8"

addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC3")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.1")
addSbtPlugin("com.dwijnand" % "sbt-dynver" % "1.2.0")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")

scriptedSettings
scriptedBufferLog := false
scriptedLaunchOpts := Seq(
  "-Dplugin.version=" + version.value
)
