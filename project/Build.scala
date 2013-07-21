import sbt._
import Keys._

object akkascale extends Build {
  val projectName = "akkascale"

  override lazy val settings = super.settings ++ Seq(resolvers := Seq())

  val akkaVersion = "2.2.0"

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaRemote = "com.typesafe.akka" %% "akka-remote" % akkaVersion
  val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion
  val slf4j = "org.slf4j" % "slf4j-api" % "1.7.5"

  // Test Libs
  val scalaTest = "org.scalatest" %% "scalatest" % "2.0.M6-SNAP33" % "test"
  val junit = "junit" % "junit" % "4.11" % "test"
  val mockito = "org.mockito" % "mockito-all" % "1.9.5" % "test"

  val publishedScalaSettings = Seq(
    scalaVersion := "2.10.2",
    resolvers ++= Seq(
      //Resolver.sonatypeRepo("snapshots"),
      //"Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
     ),

    libraryDependencies ++= Seq(
      slf4j,
      akkaActor,
      akkaRemote,
      akkaSlf4j,

      // Testing Libs
      akkaTestKit,
      scalaTest,
      junit,
      mockito))

  lazy val root = Project(
    id = projectName,
    base = file("."),
    settings = Project.defaultSettings ++ publishedScalaSettings)

}

