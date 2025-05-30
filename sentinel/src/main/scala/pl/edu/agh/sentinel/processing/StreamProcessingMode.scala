package pl.edu.agh.sentinel
package processing

sealed trait StreamProcessingMode

object StreamProcessingMode {
  /**
   * Real-time processing mode processes events as they arrive.
   * This mode is suitable for applications that require immediate processing and response to events.
   */
  case object RealTime extends StreamProcessingMode

  /**
   * Batch processing mode processes events in batches at regular intervals.
   * This mode is suitable for applications that can tolerate some delay in processing and require periodic updates. F.ex 5min
   */
  case object Batch extends StreamProcessingMode

  /**
   * Hybrid processing mode combines both real-time and batch processing.
   * This mode is suitable for applications that require both immediate processing and periodic updates.
   */
  case object Hybrid extends StreamProcessingMode
}