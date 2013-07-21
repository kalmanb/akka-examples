package kalmanb.akka

import akka.actor.ActorSystem
import akka.actor.ActorLogging
import akka.actor.Actor
import akka.actor.Props
import com.typesafe.config.ConfigFactory

object Client extends App {
  val RemoteUrl = "akka.tcp://master@127.0.0.1:2552"

  println("Client Starting ...")

  val config = ConfigFactory.load
  val system = ActorSystem("client", config.getConfig("client").withFallback(config))

  system.actorOf(Props(new ClientWorker))
  system.actorOf(Props(new ClientWorker))
  system.actorOf(Props(new ClientWorker))
  system.actorOf(Props(new ClientWorker))

}

class ClientWorker extends Worker(Client.RemoteUrl)
