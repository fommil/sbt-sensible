// Copyright: 2015 - 2018 Sam Halliday
// License: http://www.gnu.org/licenses/lgpl-3.0.en.html

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
