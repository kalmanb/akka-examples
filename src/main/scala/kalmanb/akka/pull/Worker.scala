package kalmanb.akka.pull

import akka.actor.ActorLogging
import akka.actor.Actor
import akka.actor.ActorRef
import Controller.ReadyForWork
import akka.actor.actorRef2Scala

class Worker(controller: ActorRef, counter: ActorRef) extends Actor with ActorLogging {
  import Controller._
  import Tasks._

  // Tell controller I'm ready
  controller ! ReadyForWork

  def receive = {
    // Controller responds with work for me
    case i ⇒ {
      blockingTask(1000)
      log.info(s"finished $i")

      // Update our counts
      counter ! 1

      // Tell Controller I'm ready again
      controller ! ReadyForWork
    }
  }
}

object Tasks {
  def blockingTask(duration: Int): String = {
    Thread sleep duration
    "Done"
  }
}

case class Work(id: Int)
