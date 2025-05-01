package pl.edu.agh.sentinel.processing

import zio.stream.ZStream

import pl.edu.agh.sentinel.events.{ AlertEvent, TaskEvent }

trait AlertingEngine {
  def process(events: ZStream[Any, Throwable, TaskEvent]): ZStream[Any, Throwable, AlertEvent]
}
