package org.utils

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.{Duration, FiniteDuration}

object TimeUtils  {
  val atMostDuration: FiniteDuration = Duration.create(2, TimeUnit.SECONDS)
  val timeoutMills: Long = 2 * 1000
}