name := "HW-http-client"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-jackson" % "3.6.5",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "com.softwaremill.sttp" %% "core" % "1.5.11",
  "com.typesafe.akka" %% "akka-actor" % "2.5.21",
  "com.typesafe.akka" %% "akka-stream" % "2.5.21",
  "com.typesafe.akka" %% "akka-http" % "10.1.7",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.21" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.7" % Test,
  "com.typesafe.akka" %% "akka-testkit" % "2.5.21" % Test
)