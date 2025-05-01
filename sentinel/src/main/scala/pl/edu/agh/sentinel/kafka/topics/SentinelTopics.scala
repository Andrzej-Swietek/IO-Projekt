package pl.edu.agh.sentinel.kafka.topics

import pl.edu.agh.sentinel.kafka.topics.KafkaTopic

/** Enum representing the available topics in the Lesheq application. Each case in this enum holds a `KafkaTopic`
 * object, which contains information about the topic's name, number of partitions, and replication factor.
 *
 * @param topic
 *   The `KafkaTopic` object associated with the given topic.
 */
enum SentinelTopics(val topic: KafkaTopic) {
  case DeviceEvents extends SentinelTopics(KafkaTopic("events"))
  case StatusUpdates extends SentinelTopics(KafkaTopic("audits"))
  case DeviceRead extends SentinelTopics(KafkaTopic("alerts"))
  case ManualTestTopic extends SentinelTopics(KafkaTopic("status-updates"))

  def topicName: String = topic.name
  def getKafkaTopic: KafkaTopic = topic
}