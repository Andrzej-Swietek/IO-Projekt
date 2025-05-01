package pl.edu.agh.sentinel
package kafka.topics

import zio.*
import zio.kafka.admin.{ AdminClient, AdminClientSettings }
import zio.logging.*

import pl.edu.agh.sentinel.kafka.config.KafkaConfig

/** Trait defining contract for managing Kafka topics. Can be used for mocking in tests or providing alternate
  * implementations.
  */
trait KafkaTopicManager {

  /** Ensures that all topics defined in the configuration exist in the Kafka cluster. If any are missing, they will be
    * created.
    */
  def ensureTopicsExist: Task[Unit]

  /** Deletes topics that are present in Kafka but not in the configuration. Use with caution in production
    * environments.
    */
  def deleteUnusedTopics: Task[Unit]

  /** Synchronizes Kafka topics with the configuration by:
    *   - Creating missing topics
    *   - Deleting unused topics
    */
  def cleanupTopics: Task[Unit]

  /** Creates a single topic as defined in the provided KafkaTopic configuration.
    *
    * @param topic
    *   The topic to be created
    */
  def createTopic(topic: KafkaTopic): Task[Unit]
}

final class TopicManager(admin: AdminClient, kafkaConfig: KafkaConfig) extends KafkaTopicManager {
  override def ensureTopicsExist: Task[Unit] = {
    val requiredTopics: List[KafkaTopic] = kafkaConfig.topics

    for {
      existingTopicsMap <- admin.listTopics()
      existingTopics = existingTopicsMap.keySet
      missingTopics = requiredTopics.filterNot(topic => existingTopics.contains(topic.name))
      _ <- ZIO.logInfo(s"Missing Topics: ${missingTopics.toString}")
      _ <- ZIO.foreachDiscard(missingTopics)(createTopic)
      _ <- ZIO.when(missingTopics.isEmpty)(
        ZIO.logInfo("All required Kafka topics already exist.")
      )
    } yield ()
  }

  override def createTopic(topic: KafkaTopic): Task[Unit] = {
    for {
      _ <- ZIO.logInfo(s"Creating topic: ${topic.name}")
      _ <- admin.createTopic(
        AdminClient.NewTopic(
          name = topic.name,
          numPartitions = topic.partitions,
          replicationFactor = topic.replicationFactor.toShort,
        )
      )
      _ <- ZIO.logInfo(s"Topic ${topic.name} created successfully.")
    } yield ()
  }

  override def cleanupTopics: Task[Unit] = {
    for {
      _ <- ensureTopicsExist
      _ <- deleteUnusedTopics
    } yield ()
  }

  override def deleteUnusedTopics: Task[Unit] = {
    val requiredTopicNames = kafkaConfig.topics.map(_.name).toSet

    for {
      existingTopicsMap <- admin.listTopics()
      existingTopics = existingTopicsMap.keySet
      topicsToDelete = existingTopics.diff(requiredTopicNames)
      _ <- ZIO.foreachDiscard(topicsToDelete) { topicName =>
        ZIO.logWarning(s"Deleting unused topic: $topicName") *>
          admin.deleteTopic(topicName) *>
          ZIO.logInfo(s"Topic $topicName deleted.")
      }
    } yield ()
  }
}
object TopicManager {
  final case class KafkaTopicValidationError(error: String)

  def layer: ZLayer[KafkaConfig & Scope, Throwable, TopicManager] = {
    ZLayer.scoped {
      for {
        config <- ZIO.service[KafkaConfig]
        admin <- AdminClient.make(AdminClientSettings(config.bootstrapServers))
      } yield TopicManager(admin, config)
    }
  }
}
