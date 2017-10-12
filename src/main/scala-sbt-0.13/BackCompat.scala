// Copyright: 2017 Sam Halliday
// License: http://www.gnu.org/licenses/gpl.html
package fommil

import sbt._

trait BackCompat {
  type ExclusionRule = SbtExclusionRule
  val ExclusionRule = SbtExclusionRule

  implicit class BackCompatDepOverrides[A](val deps: Set[A]) {
    def compat: Set[A] = deps
  }
}
