scalaVersion in ThisBuild := "2.12.6"

crossScalaVersions := Seq(scalaVersion.value, "2.11.12", "2.10.7")

sonatypeGithost := (Gitlab, "fommil", "sbt-sensible")
licenses := Seq(Apache2)
