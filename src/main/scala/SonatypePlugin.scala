// Copyright 2015 - 2016 Sam Halliday
// License: http://www.apache.org/licenses/LICENSE-2.0
package fommil

import java.util.Calendar

import com.typesafe.sbt.pgp.PgpKeys
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._

import sbt._
import sbt.Keys._

/**
 * Zero magic support for publishing to sonatype, assuming the project
 * is published on github or gitlab.
 *
 * If the version string contains "SNAPSHOT", it goes to snapshots,
 * otherwise to staging.
 *
 * The environment variables `SONATYPE_USERNAME` and
 * `SONATYPE_PASSWORD` will be read for credentials if there is no
 * `~/.m2/credentials` file.
 *
 * Snapshot releases can be made using `sbt publish` but staged
 * releases must be made with `sbt publishSigned` (and may require the
 * user to provide their GPG credentials manually).
 *
 * Staged releases should be followed by `sonatypeRelease`.
 *
 * Note that this will automatically set the sbt-header settings to
 * sensible defaults and align everything with the sonatype data.
 */
object SonatypePlugin extends AutoPlugin {
  override def requires = xerial.sbt.Sonatype // to override publishTo
  override def trigger  = allRequirements

  val autoImport = SonatypeKeys
  import autoImport._

  private val year = Calendar.getInstance.get(Calendar.YEAR)

  override lazy val buildSettings = Seq(
    sonatypeDevelopers := Nil
  )

  override lazy val projectSettings = Seq(
    PgpKeys.useGpgAgent := true,
    // headerMappings := Map(
    //   HeaderFileType.scala -> SensibleLineComment,
    //   HeaderFileType.java  -> SensibleLineComment
    // ),
    // BUG: https://github.com/sbt/sbt-header/issues/139 requires a blank line after
    headerMappings := headerMappings.value +
      (HeaderFileType.scala -> HeaderCommentStyle.CppStyleLineComment) +
      (HeaderFileType.java  -> HeaderCommentStyle.CppStyleLineComment),
    headerLicense := {
      assert(
        licenses.value.nonEmpty,
        "licenses cannot be empty or maven central will reject publication"
      )
      val (host, org, repo) = sonatypeGithost.value
      val start = startYear.value.map { y =>
        s"$y - "
      }.getOrElse("")
      val copyrightBlurb =
        (host, sonatypeDevelopers.value) match {
          case (_, named) if named.nonEmpty =>
            s"Copyright: $start$year ${named.mkString(", ")}"
          case (Github, _) =>
            s"Copyright: $start$year https://github.com/$org/$repo/graphs/contributors"
          case (Gitlab, _) =>
            s"Copyright: $start$year https://gitlab.com/$org/$repo/graphs/master"
        }
      // doesn't support multiple licenses
      val licenseBlurb = licenses.value.map {
        case (name, url) => s"License: $url"
      }.head
      Some(HeaderLicense.Custom(s"$copyrightBlurb\n$licenseBlurb"))
    },
    publishArtifact in Test := false,
    homepage := {
      val (host, org, repo) = sonatypeGithost.value
      val landing = host match {
        case Github => s"http://github.com/$org/$repo"
        case Gitlab => s"http://gitlab.com/$org/$repo"
      }
      Some(url(landing))
    },
    publishTo := {
      assert(
        licenses.value.nonEmpty,
        "licenses cannot be empty or maven central will reject publication"
      )
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    credentials ++= {
      val config = Path.userHome / ".m2" / "credentials"
      if (config.exists) Seq(Credentials(config))
      else {
        for {
          username <- sys.env.get("SONATYPE_USERNAME")
          password <- sys.env.get("SONATYPE_PASSWORD")
        } yield
          Credentials("Sonatype Nexus Repository Manager",
                      "oss.sonatype.org",
                      username,
                      password)
      }.toSeq
    },
    pomExtra := {
      val (host, org, repo) = sonatypeGithost.value
      val connection = host match {
        case Github => s"scm:git:git@github.com:$org/$repo.git"
        case Gitlab => s"scm:git:git@gitlab.com:$org/$repo.git"
      }

      val devs = sonatypeDevelopers.value match {
        case Nil => <id>{ org }</id>
        case named =>
          named.map { name =>
            <id> { name } </id>
          }
      }

      <scm>
        <url> { homepage.value } </url>
        <connection>{ connection }</connection>
      </scm>
      <developers>
        <developer>
          { devs }
        </developer>
      </developers>
    }
  )
}

object SonatypeKeys {
  sealed trait Githost
  case object Github extends Githost
  case object Gitlab extends Githost

  val sonatypeGithost = settingKey[(Githost, String, String)](
    "The (host, user, repository) that hosts the project at user/repository"
  )

  val sonatypeDevelopers = settingKey[List[String]](
    "Copyright owners, if non-empty will affect headers and POM file."
  )

  /*
   * Not technically keys, but are useful license definitions that can
   * be used. MIT and BSD and niche licenses are intentionally not
   * listed to discourage their use.
   *
   * At least one license must exist in the standard sbt `licenses`
   * setting. The contents of the URL should be present in a LICENSE
   * file on the repo.
   *
   * Listed in decreasing order of copyleft.
   *
   * For a quick whirlwind tour of licensing, see
   * http://fommil.github.io/scalasphere16/
   */
  val GPL3 = ("GPL 3.0" -> url("http://www.gnu.org/licenses/gpl-3.0.en.html"))
  val LGPL3 = ("LGPL 3.0" -> url(
    "http://www.gnu.org/licenses/lgpl-3.0.en.html"
  ))
  val MPL2 = ("MPL 2.0" -> "https://www.mozilla.org/en-US/MPL/2.0/")
  val Apache2 = ("Apache-2.0" -> url(
    "http://www.apache.org/licenses/LICENSE-2.0"
  ))

  // https://github.com/sbt/sbt-header/issues/137
  // doesn't aggressively nuke anything below the copyright / license line
  // case object SensibleLineComment extends CommentStyle {
  //   override val commentCreator = new LineCommentCreator("//")
  //   override val pattern: Regex =
  //     "(?s)(// Copyright[^\\n]*[\\n]// Licen[cs]e[^\\n]*[\\n])(.*)".r
  // }

}
