package pl.edu.agh.sentinel.kafka

import zio.*
import zio.json.{ JsonDecoder, JsonEncoder }
import zio.kafka.serde.{ Deserializer, Serde, Serializer }
import zio.test.{ assertTrue, Spec, TestAspect, TestEnvironment, ZIOSpecDefault }
import zio.test.Assertion.*

import pl.edu.agh.sentinel.kafka.config.KafkaConfig
import pl.edu.agh.sentinel.kafka.consumers.ConsumingStrategy
import pl.edu.agh.sentinel.kafka.consumers.ConsumingStrategy.Earliest
import pl.edu.agh.sentinel.kafka.serdes.ZioJsonSerde
import pl.edu.agh.sentinel.kafka.topics.{ KafkaTopic, TopicManager }

object KafkaProducerTest extends ZIOSpecDefault {
  given Serializer[Any, String] = Serde.string
  given Deserializer[Any, String] = Serde.string
  given Serializer[Any, KafkaTestEntity] = ZioJsonSerde[KafkaTestEntity]
  given Deserializer[Any, KafkaTestEntity] = ZioJsonSerde[KafkaTestEntity]

  private val testTopic = KafkaTopic("test-topic")

  val testEnvLayer: ZLayer[Scope, Throwable, KafkaTestEnv] =
    KafkaTestEnv.layer(Earliest)

  val kafkaConfigLayerFromEnv: ZLayer[KafkaTestEnv, Nothing, KafkaConfig] =
    ZLayer.fromZIO(ZIO.serviceWith[KafkaTestEnv](_.config))

  val topicManagerLayer: ZLayer[KafkaTestEnv & Scope, Throwable, TopicManager] =
    (kafkaConfigLayerFromEnv ++ ZLayer.fromZIO(ZIO.scope)) >>> TopicManager.layer

  val testLayers: ZLayer[Scope, Throwable, KafkaTestEnv & TopicManager] =
    testEnvLayer >+> kafkaConfigLayerFromEnv >+> topicManagerLayer

  override def spec: Spec[TestEnvironment & Scope, Any] = {
    suite("KafkaProducerTest")(
      test("should start Kafka container") {
        for {
          env <- ZIO.service[KafkaTestEnv]
          _ <- ZIO.logInfo(s"${env.config}")
          _ <- ZIO.logInfo(s"Kafka bootstrap: ${env.container.getBootstrapServers}")
          isContainerRunning <- ZIO.attempt(env.container.isRunning).orDie
        } yield assertTrue(isContainerRunning)
      },
      test("should create topic and send message") {
        for {
          env <- ZIO.service[KafkaTestEnv]
          topicManager <- ZIO.service[TopicManager]
          producer <- env.getProducer

          _ <- topicManager
            .createTopic(testTopic)
            .tapError(e => ZIO.logError(s"Topic creation failed: $e"))
            .retry(Schedule.recurs(3))
            .orDie
          _ <- ZIO.logInfo(s"Created topic: ${testTopic.name}")

          meta <- producer
            .produce(
              testTopic.name,
              "test-key",
              "test-value",
            )
            .tapBoth(
              err => ZIO.logError(s"Production failed: $err"),
              meta => ZIO.logInfo(s"Sent to ${meta.topic}-${meta.partition}@${meta.offset}"),
            )
        } yield assertTrue(meta.offset >= 0L)
      },
      test("should produce and consume a single message") {
        for {
          env <- ZIO.service[KafkaTestEnv]
          topicManager <- ZIO.service[TopicManager]
          producer <- env.getProducer
          consumer <- env.getConsumer(ConsumingStrategy.Earliest)

          _ <- topicManager.createTopic(testTopic).orDie

          _ <- producer.produce(
            testTopic.name,
            "consume-key",
            "consume-value",
          )

          messageOpt <- consumer
            .stream[String, String](testTopic.name)
            .take(1)
            .runHead
        } yield assertTrue(messageOpt.exists(_.value == "consume-value"))
      },
      test("should produce and consume message with empty key and value") {
        for {
          env <- ZIO.service[KafkaTestEnv]
          topicManager <- ZIO.service[TopicManager]
          producer <- env.getProducer
          consumer <- env.getConsumer(ConsumingStrategy.Earliest)

          _ <- topicManager.createTopic(testTopic).orDie

          _ <- producer.produce(
            testTopic.name,
            "",
            "",
          )

          messageOpt <- consumer
            .stream[String, String](testTopic.name)
            .take(1)
            .runHead
        } yield assertTrue(messageOpt.exists(msg => msg.key == "" && msg.value == ""))
      },
      test("should produce and consume multiple messages in order") {
        val messages = List("msg-1", "msg-2", "msg-3")

        for {
          env <- ZIO.service[KafkaTestEnv]
          topicManager <- ZIO.service[TopicManager]
          producer <- env.getProducer
          consumer <- env.getConsumer(ConsumingStrategy.Earliest)

          _ <- topicManager.createTopic(testTopic).orDie

          _ <- ZIO.foreachDiscard(messages) { msg =>
            producer.produce(testTopic.name, "ordered-key", msg)
          }

          consumed <- consumer
            .stream[String, String](testTopic.name)
            .filter(_.key == "ordered-key")
            .map(_.value)
            .take(messages.size.toLong)
            .runCollect

        } yield assertTrue(consumed.toList == messages)
      },
      test("should produce and consume multiple complex messages in order") {
        val messages = List(
          KafkaTestEntity(groups = Option(Set("group1", "group2"))),
          KafkaTestEntity(groups = Option(Set("group2", "group3"))),
          KafkaTestEntity(groups = Option(Set("group3", "group4"))),
        )

        for {
          env <- ZIO.service[KafkaTestEnv]
          topicManager <- ZIO.service[TopicManager]
          producer <- env.getProducer
          consumer <- env.getConsumer(ConsumingStrategy.Earliest)

          _ <- topicManager.createTopic(testTopic).orDie

          _ <- ZIO.foreachDiscard(messages) { msg =>
            producer.produce(testTopic.name, "ordered-key", msg)
          }

          consumed <- consumer
            .stream[String, KafkaTestEntity](testTopic.name)
            .filter(_.key == "ordered-key")
            .map(_.value)
            .take(messages.size.toLong)
            .runCollect

        } yield assertTrue(consumed.toList == messages)
      },
      test("should produce KafkaTestEntity message") {
        val entity = KafkaTestEntity(
          groups = Option(Set("group1", "group2")),
          values = Map("metric1" -> 3.14, "metric2" -> 2.71),
        )

        for {
          env <- ZIO.service[KafkaTestEnv]
          topicManager <- ZIO.service[TopicManager]
          producer <- env.getProducer

          _ <- topicManager.createTopic(testTopic).orDie

          meta <- producer
            .produce(
              testTopic.name,
              "entity-key",
              entity,
            )
        } yield assertTrue(meta.offset >= 0L)
      },
    ).provideLayer(
      Scope.default >+>
        testLayers
    ) @@ TestAspect.timeout(60.seconds)
  }
}
