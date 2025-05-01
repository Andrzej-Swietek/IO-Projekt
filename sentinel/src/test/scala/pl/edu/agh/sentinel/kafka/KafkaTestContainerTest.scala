package pl.edu.agh.sentinel.kafka

import pl.edu.agh.sentinel.kafka.consumers.ConsumingStrategy.Earliest
import zio.*
import zio.test.*
object KafkaTestContainerTest extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment & Scope, Any] = {
    suite("KafkaTestContainerTest")(
      test("Kafka container should start and be available") {
        for {
          env <- ZIO.service[KafkaTestEnv]
          result <- ZIO.attempt(env.container.nn.isRunning)
        } yield assertTrue(result)
      },
      test("Should have running container and return bootstrap servers") {
        for {
          env <- ZIO.service[KafkaTestEnv]
          bootstrap <- ZIO.attempt(env.container.nn.getBootstrapServers)
        } yield assertTrue(bootstrap.nn.startsWith("PLAINTEXT://") || bootstrap.nn.nonEmpty)
      },
    ).provideLayer(KafkaTestEnv.layer(Earliest))
  }
}
