ivyLoggingLevel := UpdateLogging.Quiet
scalacOptions in Compile ++= Seq("-feature", "-deprecation")

addSbtPlugin("com.fommil"   % "sbt-sensible" % "2.4.1")
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.4.0")
