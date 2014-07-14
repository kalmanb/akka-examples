organization := "com.kalmanb"

name := "akkaexamples"

scalaVersion := "2.11.1"
      
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.4",
  "com.typesafe.akka" %% "akka-remote" % "2.3.4",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.4",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.4",
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.scalaz" %% "scalaz-core" % "7.1.0-RC1",
  "com.chuusai" %% "shapeless" % "2.0.0"
)

lazy val root = project.in(file(".")).dependsOn(testSpecs % "test->test")

lazy val testSpecs = RootProject(uri("git://github.com/kalmanb/scalatest-specs.git#0.1.1"))

