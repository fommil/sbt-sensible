ivyLoggingLevel := UpdateLogging.Quiet
scalacOptions in Compile ++= Seq("-feature", "-deprecation")

addSbtPlugin("com.fommil" % "sbt-sensible" % "2.1.1")
addSbtPlugin("com.lucidchart" % "sbt-scalafmt-coursier" % "1.14")

libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value
