package kalmanb.akka.pull

import scala.collection.mutable.ListBuffer
import akka.actor.Actor
import akka.actor.actorRef2Scala
import akka.actor.ActorLogging

object Controller {
  case object ReadyForWork
}

class Controller extends Actor with ActorLogging {
  import Controller._

  // Line up some work to do
  val workToDo = (1 to 1000).to[ListBuffer]

  def receive = {
    case ReadyForWork ⇒ {
      if (workToDo.size > 0) {
        sender ! Work(workToDo.head)
        workToDo -= workToDo.head
      } else {
        log.info("No more work")
      }
    }
  }

}
