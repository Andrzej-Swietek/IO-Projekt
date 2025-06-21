package pl.edu.agh.sentinel.kafka.topics

import pl.edu.agh.sentinel.kafka.topics.KafkaTopic

/** Enum representing the available topics in the Lesheq application. Each case in this enum holds a `KafkaTopic`
  * object, which contains information about the topic's name, number of partitions, and replication factor.
  *
  * @param topic
  *   The `KafkaTopic` object associated with the given topic.
  */
enum SentinelTopics(val topic: KafkaTopic) {
  case Events extends SentinelTopics(KafkaTopic("events"))
  case TaskEvents extends SentinelTopics(KafkaTopic("task-events"))
  case UserActivity extends SentinelTopics(KafkaTopic("user-activity-events"))
  case Audits extends SentinelTopics(KafkaTopic("audits"))
  case UserStats extends SentinelTopics(KafkaTopic("user-stats"))
  case TeamStats extends SentinelTopics(KafkaTopic("team-stats"))
  case Alerts extends SentinelTopics(KafkaTopic("alerts"))
  case StatusUpdate extends SentinelTopics(KafkaTopic("status-updates"))
  case ManualTestTopic extends SentinelTopics(KafkaTopic("manual-test-topic"))

  def topicName: String = topic.name
  def getKafkaTopic: KafkaTopic = topic
}
