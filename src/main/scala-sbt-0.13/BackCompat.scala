// Copyright: 2015 - 2017 https://gitlab.com/fommil/sbt-sensible/graphs/master
// License: http://www.apache.org/licenses/LICENSE-2.0

package fommil

import sbt._

trait BackCompat {
  type ExclusionRule = SbtExclusionRule
  val ExclusionRule = SbtExclusionRule

  implicit class BackCompatDepOverrides[A](val deps: Set[A]) {
    def compat: Set[A] = deps
  }

  implicit class BackCompatForkRun(val result: Option[String]) {
    def dealWithIt(): Unit = result.foreach(sys.error)
  }
}
