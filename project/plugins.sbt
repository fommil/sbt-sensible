ivyLoggingLevel := UpdateLogging.Quiet
scalacOptions in Compile ++= Seq("-feature", "-deprecation")

addSbtPlugin("com.fommil" % "sbt-sensible" % "2.0.0")
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC12")
addSbtPlugin("com.lucidchart" % "sbt-scalafmt-coursier" % "1.12")

libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value
