// Copyright: 2015 - 2017 https://gitlab.com/fommil/sbt-sensible/graphs/master
// License: http://www.apache.org/licenses/LICENSE-2.0

package fommil

import scala.collection.immutable.Set

trait BackCompat {
  implicit class BackCompatDepOverrides[A](val deps: Set[A]) {
    def compat: Seq[A] = deps.toSeq
  }
}
