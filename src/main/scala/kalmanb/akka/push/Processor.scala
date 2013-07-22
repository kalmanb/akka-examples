package kalmanb.akka.push

import akka.actor.ActorLogging
import akka.actor.Actor

class Processor extends Actor with ActorLogging {

  def receive = {
    case toBeProcessed ⇒ {
      log.info(s"Processing $toBeProcessed")
      // Do some work
      Thread sleep 500
      sender ! s"here's a processed result $toBeProcessed"
    }
  }
}
