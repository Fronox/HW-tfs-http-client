package util

import com.typesafe.scalalogging.{LazyLogging, Logger}

trait ImplicitLogger extends LazyLogging {
  implicit lazy val implicitLogger: Logger = logger
}
