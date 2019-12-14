name := "scala-web-service"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.26",
  "com.typesafe.akka" %% "akka-http" % "10.1.11",
  "com.typesafe.akka" %% "akka-stream" % "2.5.26",

  "org.mongodb.scala" %% "mongo-scala-driver" % "2.7.0",
  "ch.rasc" % "bsoncodec" % "1.0.1",

  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11"

)
