// Copyright: 2015 - 2018 Sam Halliday
// License: http://www.gnu.org/licenses/lgpl-3.0.en.html

package fommil

import scala.collection.immutable.Set
import scala.util.Try

trait BackCompat {
  implicit class BackCompatDepOverrides[A](val deps: Set[A]) {
    def compat: Seq[A] = deps.toSeq
  }

  implicit class BackCompatForkRun(val result: Try[Unit]) {
    def dealWithIt(): Unit = result.failed.foreach(f => sys.error(f.getMessage))
  }
}
