package pl.edu.agh.sentinel
package kafka

import zio.*

enum RetryPolicy(val schedule: zio.Schedule[Any, Throwable, Any]) {
  case None extends RetryPolicy(zio.Schedule.stop)
  case Forever extends RetryPolicy(zio.Schedule.forever)
  case Spaced3sForever extends RetryPolicy((zio.Schedule.spaced(3.seconds) && zio.Schedule.forever))
  case Exponential extends RetryPolicy(zio.Schedule.exponential(1.second))

  def isNone: Boolean = this == RetryPolicy.None
  def getSchedule = schedule
}