ivyLoggingLevel := UpdateLogging.Quiet
scalacOptions in Compile ++= Seq("-feature", "-deprecation")

addSbtPlugin("com.fommil" % "sbt-sensible" % "1.2.3")
addSbtPlugin("com.lucidchart" % "sbt-scalafmt-coursier" % "1.12")

libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value
