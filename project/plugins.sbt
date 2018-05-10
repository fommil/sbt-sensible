ivyLoggingLevel := UpdateLogging.Quiet
scalacOptions in Compile ++= Seq("-feature", "-deprecation")

addSbtPlugin("com.fommil"   % "sbt-sensible" % "2.4.5")
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.5.1")
