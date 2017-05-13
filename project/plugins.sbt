ivyLoggingLevel := UpdateLogging.Quiet
scalacOptions in Compile ++= Seq("-feature", "-deprecation")
addSbtPlugin("com.fommil" % "sbt-sensible" % "1.1.16")

// sbt-sonatype needs commons-logging (fixed when using sbt-sensible 1.1.17)
val slf4jVersion = "1.7.25"
libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.slf4j" % "slf4j-api" % slf4jVersion,
  "org.slf4j" % "jul-to-slf4j" % slf4jVersion,
  "org.slf4j" % "jcl-over-slf4j" % slf4jVersion,
  "org.slf4j" % "log4j-over-slf4j" % slf4jVersion
)

libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value
