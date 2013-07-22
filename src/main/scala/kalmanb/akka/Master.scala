package kalmanb.akka

import akka.actor.ActorSystem
import akka.actor.Props

object Master extends App {
  println("Master Starting ...")

  val system = ActorSystem("master")

  val controller = system.actorOf(Props(new Controller), name = "controller")
  val counter = system.actorOf(Props(new Counter), name = "counter")

  (1 to 1).foreach { i =>
    system.actorOf(Props(new Worker(controller, counter)))
  }

  Common.shutdown(system)
}
