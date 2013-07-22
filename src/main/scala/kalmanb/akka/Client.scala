package kalmanb.akka

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.actor.Props

object Client extends App {
  val RemoteUrl = "akka.tcp://master@127.0.0.1:2552"

  println("Client Starting ...")

  // Load config for client
  val config = ConfigFactory.load
  val system = ActorSystem("client", config.getConfig("client").withFallback(config))

  // Note "actorFor" is a lookup - not creation
  val controller = system.actorFor(RemoteUrl + "/user/controller")
  val counter = system.actorFor(RemoteUrl + "/user/counter")

  (1 to 10).foreach { i ⇒
    system.actorOf(Props(new Worker(controller, counter)))
  }

  Common.shutdown(system)
}

