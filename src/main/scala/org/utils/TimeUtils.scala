package org.utils

import scala.concurrent.duration.{DurationInt, FiniteDuration}

object TimeUtils  {
  val atMostDuration: FiniteDuration = 2.seconds
  val timeoutMills: Long = 2 * 1000
}