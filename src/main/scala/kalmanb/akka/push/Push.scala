package kalmanb.akka.push

import akka.actor.ActorLogging
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.routing.RoundRobinRouter
import kalmanb.akka.Common
import akka.actor.Actor
import akka.actor.Status

object Push extends App {
  println("Push Starting ...")

  val system = ActorSystem("master")

  val db = system.actorOf(Props(new Db))
  val processor = system.actorOf(Props(new Processor))
  val webserver = system.actorOf(Props(new WebServer(db, processor)))

  (1 to 100).foreach { i ⇒
    webserver ! WebServer.Request("/page/" + i)
  }

  Common.shutdown(system)

  // Now we have a simple system, how do we scale up?

  // Now how about out?
}

object PushError extends App {
  println("PushError Starting ...")

  val system = ActorSystem("master")

  val db = system.actorOf(Props(new Db))
  val processor = system.actorOf(Props(new Error))
  val webserver = system.actorOf(Props(new WebServer(db, processor)))

  (1 to 1).foreach { i ⇒
    webserver ! WebServer.Request("/page/" + i)
  }

  Common.shutdown(system)

  class Error extends Actor {
    def receive = {
      case _ => {
        val e = new Exception("error")
        // Send it back to sender
        sender ! new Status.Failure(e)
      }
    }
  }
}

object PushRouted extends App {
  println("PushRouted Starting ...")

  val system = ActorSystem("master")

  val db = system.actorOf(Props(new Db).withRouter(RoundRobinRouter(nrOfInstances = 10)))
  val processor = system.actorOf(Props(new Processor).withRouter(RoundRobinRouter(nrOfInstances = 10)))
  val webserver = system.actorOf(Props(new WebServer(db, processor)))

  (1 to 100).foreach { i ⇒
    webserver ! WebServer.Request("/page/" + i)
  }

  Common.shutdown(system)
}

object PushRemoteMaster extends App {
  println("PushRemoteMaster Starting ...")

  val system = ActorSystem("master")

  val db = system.actorOf(Props(new Db).withRouter(RoundRobinRouter(nrOfInstances = 10)))
  val processor = system.actorOf(Props(new Processor).withRouter(RoundRobinRouter(nrOfInstances = 10)))
  val webserver = system.actorOf(Props(new WebServer(db, processor)))

  (1 to 100).foreach { i ⇒
    webserver ! WebServer.Request("/page/" + i)
  }

  Common.shutdown(system)
}
