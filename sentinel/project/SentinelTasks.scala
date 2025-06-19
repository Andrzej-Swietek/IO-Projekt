import sbt.Keys.*
import sbt.{Def, *}

object SentinelTasks {
  lazy val benchmarkKafka = taskKey[Unit]("Run only Kafka benchmark tests")

  lazy val settings: Seq[Def.Setting[Task[Unit]]] = Seq(
    benchmarkKafka := {
      val _ = (Test / testOnly).toTask(" *KafkaBenchmarkTest").value
    }
  )
}
