package util

import com.typesafe.scalalogging.Logger

object Measure {
  def apply[A](f: => A)(implicit logger: Logger): A = {
    val start = System.currentTimeMillis()
    val r = f
    val end = System.currentTimeMillis()
    logger.info(s"Operation took: ${(end.toDouble - start.toDouble) / 1000} seconds")
    r
  }
}
