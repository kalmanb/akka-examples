package kalmanb.akka.pull

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import kalmanb.akka.Common
import akka.actor.Props

object PullClient extends App {
  val RemoteUrl = "akka.tcp://master@127.0.0.1:2552"

  println("PullClient Starting ...")

  // Load config for client
  val config = ConfigFactory.load
  val system = ActorSystem("client", config.getConfig("client").withFallback(config))

  // Note "actorFor" is a lookup - not creation
  val controller = system.actorFor(RemoteUrl + "/user/controller")
  val counter = system.actorFor(RemoteUrl + "/user/counter")

  // Start 10 workers
  (1 to 10).foreach { i ⇒
    system.actorOf(Props(new Worker(controller, counter)))
  }

  Common.shutdown(system)
}

