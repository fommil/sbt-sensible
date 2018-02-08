// Copyright: 2015 - 2018 https://gitlab.com/fommil/sbt-sensible/graphs/master
// License: http://www.apache.org/licenses/LICENSE-2.0

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
