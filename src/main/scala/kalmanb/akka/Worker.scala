package kalmanb.akka

import akka.actor.ActorLogging
import akka.actor.Actor
import akka.actor.ActorRef

class Worker(controller: ActorRef, counter: ActorRef) extends Actor with ActorLogging {
  import Controller._

  // Tell controller I'm ready
  controller ! ReadyForWork

  def receive = {
    // Controller responds with work for me
    case i ⇒ {
      Common.blockingTask(1000)
      log.info(s"finished $i")

      // Update our counts
      counter ! 1

      // Tell Controller I'm ready again
      controller ! ReadyForWork
    }
  }
}

case class Work(id: Int)