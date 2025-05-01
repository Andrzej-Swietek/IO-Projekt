package pl.edu.agh.sentinel.kafka

import zio.*
import zio.kafka.producer.ProducerSettings
import org.testcontainers.containers.Network
import org.testcontainers.kafka.KafkaContainer
import pl.edu.agh.sentinel.kafka.config.KafkaConfig
import pl.edu.agh.sentinel.kafka.{config, consumers}
import pl.edu.agh.sentinel.kafka.consumers.{ConsumingStrategy, KafkaConsumer}
import pl.edu.agh.sentinel.kafka.producers.KafkaProducer


final case class KafkaTestEnv(
  network: Network,
  container: KafkaContainer,
  config: KafkaConfig,
) {
  def getProducer: ZIO[Scope, Throwable, KafkaProducer] = {
    KafkaProducer.fromSettings(
      ProducerSettings(config.bootstrapServers)
        .withProperty("client.id", config.clientId)
        .withProperties(config.producerProperties)
    )
  }

  def getConsumer(strategy: ConsumingStrategy): ZIO[Scope, Throwable, KafkaConsumer] = {
    KafkaConsumer.fromSettings(
      KafkaConsumer.settingsWithOffset(
        config,
        strategy,
      )
    )
  }
}

object KafkaTestEnv {

  def layer(strategy: ConsumingStrategy): ZLayer[Scope, Throwable, KafkaTestEnv] = {
    ZLayer.scoped {
      for {
        network <- ZIO.acquireRelease(ZIO.succeed(Network.newNetwork().nn))(n => ZIO.attempt(n.close()).ignore)
        container <- ZIO
          .attempt {
            new KafkaContainer("apache/kafka:latest")
              .withNetwork(network)
              .nn
              .withReuse(false)
              .nn
              .withStartupTimeout(java.time.Duration.ofMinutes(5))
              .nn
          }
          .tap(container => ZIO.attempt(container.start()))
        bootstrapServers <- ZIO.attempt(container.getBootstrapServers.nn).orDie
        _ <- ZIO.logInfo(s"[TEST ENV]: Kafka running at: $bootstrapServers")
        config <- ZIO.succeed(
          KafkaConfig(
            bootstrapServers = bootstrapServers.split(",").nn.map(_.nn.trim.nn).toList,
            groupId = "test-group",
            clientId = "test-client",
            autoOffsetReset = strategy.strategy,
            topics = List.empty,
            producerProperties = Map.empty,
          )
        )
      } yield KafkaTestEnv(network, container.nn, config)
    }
  }
}