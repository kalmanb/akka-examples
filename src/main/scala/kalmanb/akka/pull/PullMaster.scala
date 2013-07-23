package kalmanb.akka.pull

import akka.actor.ActorSystem
import kalmanb.akka.Common
import akka.actor.Props

object PullMaster extends App {
  println("PullMaster Starting ...")

  val system = ActorSystem("master")

  val controller = system.actorOf(Props(new Controller), name = "controller")
  val counter = system.actorOf(Props(new Counter), name = "counter")

  // Start workers - just one for now
  (1 to 1).foreach { i ⇒
    system.actorOf(Props(new Worker(controller, counter)))
  }

  Common.shutdown(system)
}
