// Copyright 2015 - 2016 Sam Halliday
// License: http://www.apache.org/licenses/LICENSE-2.0
package fommil

import java.util.concurrent.atomic.AtomicLong

import scala.util.Properties

import sbtdynver.DynVerPlugin.autoImport._
import sbt._
import sbt.Keys._

/**
 * A bunch of sensible sbt defaults used by https://github.com/fommil
 */
object SensiblePlugin extends AutoPlugin with BackCompat {
  override def requires = sbtdynver.DynVerPlugin
  override def trigger  = allRequirements

  val autoImport = SensibleSettings
  import autoImport._

  private val JavaSpecificFlags =
    sys.props("java.version").take(3) match {
      case "1.6" | "1.7" => List("-XX:MaxPermSize=256m")
      case _             => List("-XX:MaxMetaspaceSize=256m")
    }

  override val buildSettings = Seq(
    maxErrors := 1,
    fork := true,
    cancelable := true,
    sourcesInBase := false,
    javaOptions += s"-Dsbt.sensible.root=${(baseDirectory in ThisBuild).value.getCanonicalFile}",
    version := {
      val dyn        = dynverGitDescribeOutput.value.version(dynverCurrentDate.value)
      val isSnapshot = dynverGitDescribeOutput.value.isSnapshot
      if (!isSnapshot) dyn
      else {
        val SemVer = "(\\d+)[.](\\d+)[.](\\d+)(-[MR].*)?".r // accounts for M1 / RC
        val NewVer = "(\\d+)[.](\\d+)[.](\\d+)-alpha0".r
        dyn.replaceAll("[+].*", "") match {
          case NewVer(major, minor, patch) => s"$major.$minor.$patch-SNAPSHOT"
          case SemVer(major, minor, patch) =>
            s"$major.$minor.${patch.toInt + 1}-SNAPSHOT"
          case other => "0.0.1-SNAPSHOT"
        }
      }
    },
    isSnapshot := version.value.contains("SNAP"),
    concurrentRestrictions := {
      val limited = Properties.envOrElse("SBT_TASK_LIMIT", "4").toInt
      Seq(Tags.limitAll(limited))
    }
  )

  override val projectSettings = Seq(
    ivyLoggingLevel := UpdateLogging.Quiet,
    conflictManager := ConflictManager.strict,
    // makes it really easy to use a RAM disk
    target := {
      sys.env.get("SBT_VOLATILE_TARGET") match {
        case None => target.value
        case Some(base) =>
          file(base) / target.value.getCanonicalPath.replace(':', '_')
      }
    },
    javaOptions ++= {
      sys.env.get("SBT_VOLATILE_TARGET") match {
        case None => Nil
        case Some(base) =>
          val tmpdir = s"$base/java.io.tmpdir"
          file(tmpdir).mkdirs()
          s"-Djava.io.tmpdir=$tmpdir" :: Nil
      }
    },
    javaOptions += s"-Dsbt.sensible.name=${name.value}",
    javaOptions in Compile += s"-Dlogback.configurationFile=${(baseDirectory in ThisBuild).value}/logback-main.xml",
    resources in Compile ++= {
      // automatically adds legal information to jars, but are
      // lower-cased https://github.com/fommil/sbt-sensible/issues/5
      val orig = (resources in Compile).value
      val base = baseDirectory.value
      val root = (baseDirectory in ThisBuild).value

      def fileWithFallback(name: String): File =
        if ((base / name).exists) base / name
        else if ((root / name).exists) root / name
        else throw new IllegalArgumentException(s"legal file $name must exist")

      Seq(fileWithFallback("LICENSE"), fileWithFallback("NOTICE"))
    },
    // not great with scalatest, so only use in Compile
    scalacOptions in Compile += "-Ywarn-value-discard",
    scalacOptions ++= Seq(
      "-encoding",
      "UTF-8",
      "-feature",
      "-deprecation",
      "-Xfuture",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-nullary-unit",
      "-Ywarn-numeric-widen",
      "-Ywarn-inaccessible"
    ) ++ {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 12)) =>
          Seq("-Xlint:-unused,_") // unused warnings must be explicitly added
        case Some((2, 11)) =>
          Seq("-Xlint", "-Yinline-warnings", "-Ywarn-unused-import")
        case Some((2, 10)) => Seq("-Xlint", "-Yinline-warnings")
        case _             => Nil
      }
    } ++ {
      // fatal warnings can get in the way during the DEV cycle
      if (sys.env.contains("CI")) Seq("-Xfatal-warnings")
      else Nil
    },
    scalacOptions in console in Compile ~= { opts =>
      opts.filterNot { flag =>
        flag.startsWith("-Ywarn") || flag.startsWith("-Xlint")
      }
    },
    javacOptions ++= Seq(
      "-Xlint:all",
      "-Xlint:-options",
      "-Xlint:-path",
      "-Xlint:-processing"
    ) ++ {
      if (sys.env.contains("CI")) Seq("-Werror")
      else Nil
    },
    // some of those flags are not supported in doc
    javacOptions in doc ~= (_.filterNot(_.startsWith("-Xlint"))),
    javacOptions in doc ~= (_.filterNot(_.startsWith("-Werror"))),
    javaOptions ++= JavaSpecificFlags ++ Seq("-Xss2m", "-Dfile.encoding=UTF8"),
    // must be project-level because of crazy ivy...
    libraryDependencies ++= sensibleTestLibs(Test),
    excludeDependencies ++= {
      // plugins tend to have an intricate network of dependencies...
      if (sbtPlugin.value) Nil
      else
        Seq(
          // we don't want another https://issues.apache.org/jira/browse/CASSANDRA-10984
          ExclusionRule("io.netty", "netty-all"),
          // clean up the mess made by everybody who doesn't use slf4j...
          ExclusionRule("org.apache.logging.log4j", "log4j-api-scala_2.10"),
          ExclusionRule("org.apache.logging.log4j", "log4j-liquibase"),
          ExclusionRule("org.apache.logging.log4j", "log4j-jul"),
          ExclusionRule("org.apache.logging.log4j", "log4j-iostreams"),
          ExclusionRule("org.apache.logging.log4j", "log4j-nosql"),
          ExclusionRule("org.apache.logging.log4j", "log4j-bom"),
          ExclusionRule("org.apache.logging.log4j", "log4j-osgi"),
          ExclusionRule("org.apache.logging.log4j", "log4j-api-scala_2.11"),
          ExclusionRule("org.apache.logging.log4j", "log4j-jmx-gui"),
          ExclusionRule("org.apache.logging.log4j", "log4j-taglib"),
          ExclusionRule("org.apache.logging.log4j", "log4j-web"),
          ExclusionRule("org.apache.logging.log4j", "log4j-flume-ng"),
          ExclusionRule("org.apache.logging.log4j", "log4j-jcl"),
          ExclusionRule("org.apache.logging.log4j", "log4j-to-slf4j"),
          ExclusionRule("org.apache.logging.log4j", "log4j-slf4j-impl"),
          ExclusionRule("org.apache.logging.log4j", "log4j-1.2-api"),
          ExclusionRule("org.apache.logging.log4j", "log4j-core-its"),
          ExclusionRule("org.apache.logging.log4j", "log4j-core"),
          ExclusionRule("org.apache.logging.log4j", "log4j-api"),
          ExclusionRule("org.apache.logging.log4j", "log4j"),
          ExclusionRule("log4j", "apache-log4j-extras"),
          ExclusionRule("log4j", "log4j"),
          ExclusionRule("commons-logging", "commons-logging"),
          ExclusionRule("commons-logging", "commons-logging-api"),
          ExclusionRule("commons-logging", "commons-logging-adapters"),
          ExclusionRule("org.slf4j", "slf4j-log4j12"),
          ExclusionRule("org.slf4j", "slf4j-jdk14"),
          ExclusionRule("org.slf4j", "slf4j-jcl.jar")
        )
    },
    dependencyOverrides ++= Set(
      // user may have a different scala provider...
      scalaOrganization.value % "scala-compiler" % scalaVersion.value,
      scalaOrganization.value % "scala-library"  % scalaVersion.value,
      scalaOrganization.value % "scala-reflect"  % scalaVersion.value,
      scalaOrganization.value % "scalap"         % scalaVersion.value
    ).compat ++ logback
  ) ++ inConfig(Test)(sensibleTestSettings) ++ inConfig(Compile)(
    sensibleCrossPath
  )

}

object SensibleSettings {
  def shapeless = Def.setting {
    val plugins = CrossVersion
      .partialVersion(scalaVersion.value)
      .collect {
        case (2, 10) =>
          compilerPlugin(
            "org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.patch
          )
      }
      .toList

    "com.chuusai" %% "shapeless" % "2.3.2" :: plugins
  }

  def sensibleTestLibs(config: Configuration) =
    Seq(
      // janino 3.0.6 is not compatible and causes http://www.slf4j.org/codes.html#replay
      "org.codehaus.janino" % "janino" % "2.7.8" % config,
      "org.scalatest"       %% "scalatest" % "3.0.4" % config
    ) ++ logback.map(_      % config)

  // WORKAROUND https://github.com/sbt/sbt/issues/2534
  // don't forget to also call testLibs
  def sensibleTestSettings = sensibleCrossPath ++ Seq(
    parallelExecution := true,
    scalacOptions in console ~= { opts =>
      opts.filterNot { flag =>
        flag.startsWith("-Ywarn") || flag.startsWith("-Xlint")
      }
    },
    javaOptions ~= (_.filterNot(_.startsWith("-Dlogback.configurationFile"))),
    javaOptions += s"-Dlogback.configurationFile=${(baseDirectory in ThisBuild).value}/logback-${configuration.value}.xml",
    testForkedParallel := true,
    testGrouping := {
      val opts = ForkOptions(
        javaHome = javaHome.value,
        outputStrategy = outputStrategy.value,
        bootJars = Vector.empty[File],
        workingDirectory = Option(baseDirectory.value),
        runJVMOptions = javaOptions.value.toVector,
        connectInput = connectInput.value,
        envVars = envVars.value
      )
      definedTests.value.map { test =>
        Tests.Group(test.name, Seq(test), Tests.SubProcess(opts))
      }
    },
    javaOptions ++= {
      if (sys.env.get("GC_LOGGING").isEmpty) Nil
      else {
        val base   = (baseDirectory in ThisBuild).value
        val config = configuration.value
        val n      = name.value
        val count  = forkCount.incrementAndGet() // subject to task evaluation
        val out    = { base / s"gc-$config-$n.log" }.getCanonicalPath
        Seq(
          // https://github.com/fommil/lions-share
          s"-Xloggc:$out",
          "-XX:+PrintGCDetails",
          "-XX:+PrintGCDateStamps",
          "-XX:+PrintTenuringDistribution",
          "-XX:+PrintHeapAtGC"
        )
      }
    },
    // and don't forget `export SCALACTIC_FILE_PATHNAMES=true`
    testOptions += Tests.Argument(TestFrameworks.ScalaTest,
                                  "-oF",
                                  "-W",
                                  "120",
                                  "60"),
    testFrameworks := Seq(TestFrameworks.ScalaTest, TestFrameworks.JUnit)
  )

  private val slf4jVersion = "1.7.25"
  val logback = Seq(
    "ch.qos.logback" % "logback-classic"  % "1.2.3",
    "org.slf4j"      % "slf4j-api"        % slf4jVersion,
    "org.slf4j"      % "jul-to-slf4j"     % slf4jVersion,
    "org.slf4j"      % "jcl-over-slf4j"   % slf4jVersion,
    "org.slf4j"      % "log4j-over-slf4j" % slf4jVersion
  )

  // used for unique gclog naming
  private val forkCount = new AtomicLong()

  // WORKAROUND https://github.com/sbt/sbt/issues/2819
  private[fommil] def sensibleCrossPath = Seq(
    unmanagedSourceDirectories += {
      val dir                  = scalaSource.value
      val Some((major, minor)) = CrossVersion.partialVersion(scalaVersion.value)
      file(s"${dir.getPath}-$major.$minor")
    }
  )

}
