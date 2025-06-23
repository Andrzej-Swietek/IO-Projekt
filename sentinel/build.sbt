ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.4"

val zioVersion = "2.1.16"
val zioHttpVersion = "3.2.0"
val zioKafkaVersion = "2.12.0"
val zioJsonVersion = "0.7.39"
val zioStreamsVersion = "2.1.15"
val zioPreludeVersion = "1.0.0-RC39"
val zioConfigVersion = "4.0.4"
val zioLoggingVersion = "2.5.0"
val logbackClassicVersion = "1.5.18"
val chimneyVersion = "1.7.3"
val testContainersVersion = "0.43.0"
val zioMockVersion = "1.0.0-RC12"
val zioMagnoliaVersion = "4.0.4"
val testcontainersVersion = "1.20.6"
val zioRedisVersion = "1.1.3"
val zioSchemaProtobufVersion = "1.6.1"

lazy val root = (project in file("."))
  .settings(
    name := "pl/edu/agh/sentinel",
    Test / testOptions += Tests.Filter(testName => !testName.toLowerCase.contains("benchmark")),
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-http" % zioHttpVersion,
      "dev.zio" %% "zio-kafka" % zioKafkaVersion,
      "dev.zio" %% "zio-streams" % zioStreamsVersion,
      "dev.zio" %% "zio-config" % zioConfigVersion,
      "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
      "dev.zio" %% "zio-json" % zioJsonVersion,
      "io.scalaland" %% "chimney" % chimneyVersion,
      "dev.zio" %% "zio-prelude" % zioPreludeVersion,
      "dev.zio" %% "zio-config-magnolia" % zioMagnoliaVersion,
      "dev.zio" %% "zio-redis" % zioRedisVersion,
      "dev.zio" %% "zio-schema-protobuf" % zioSchemaProtobufVersion,
      "com.softwaremill.sttp.client3" %% "core" % "3.8.0",
      "com.softwaremill.sttp.client3" %% "zio" % "3.11.0",
      "com.softwaremill.sttp.client3" %% "zio-json" % "3.11.0",
      "com.sun.mail" % "jakarta.mail" % "1.6.7",

      // logging
      "dev.zio" %% "zio-logging" % zioLoggingVersion,
      "dev.zio" %% "zio-logging-slf4j" % zioLoggingVersion,
      "ch.qos.logback" % "logback-classic" % logbackClassicVersion,

      // test
      "dev.zio" %% "zio-test" % zioVersion % Test,
      "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
      "dev.zio" %% "zio-test-junit" % zioVersion % Test,
      "dev.zio" %% "zio-mock" % zioMockVersion % Test,
      "dev.zio" %% "zio-test-magnolia" % zioVersion % Test,

      // Testcontainers (Kafka + core + JUnit)
      "org.testcontainers" % "testcontainers" % testcontainersVersion % Test,
      "org.testcontainers" % "kafka" % testcontainersVersion % Test,
      "com.dimafeng" %% "testcontainers-scala-kafka" % "0.40.2",
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),

    // Fat JAR
    assembly / mainClass := Some("pl.edu.agh.sentinel.SentinelApp"),
    assembly / assemblyJarName := s"sentinel-assembly-${version.value}.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    },
    scalacOptions ++= Seq(
      "-Xmax-inlines:128",
      "-Yexplicit-nulls",
      "-Yno-flexible-types",
      "-Wsafe-init",
      "-Wunused:all", // Enable warnings for all unused code (imports, locals, privates, etc.)
      "-Wnonunit-statement", // Warn when non-Unit expression results are unused
      "-explain", // Provide more detailed explanations for errors
      "-explain-types", // Show detailed type errors
      "-no-indent",
    ),
    Compile / doc / sources := Seq.empty,
  )
  .enablePlugins(JavaAppPackaging, UniversalPlugin, AssemblyPlugin)

addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
addCommandAlias("build-jar", ";clean;compile;assembly")
