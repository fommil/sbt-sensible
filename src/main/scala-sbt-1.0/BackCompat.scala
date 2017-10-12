// Copyright: 2017 Sam Halliday
// License: http://www.gnu.org/licenses/gpl.html
package fommil

import scala.collection.immutable.Set

trait BackCompat {
  implicit class BackCompatDepOverrides[A](val deps: Set[A]) {
    def compat: Seq[A] = deps.toSeq
  }
}
