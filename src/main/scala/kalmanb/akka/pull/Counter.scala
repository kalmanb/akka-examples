package kalmanb.akka.pull

import akka.actor.ActorLogging
import akka.actor.Actor
import akka.actor.actorRef2Scala
import scala.concurrent.duration.DurationInt

class Counter extends Actor with ActorLogging {
  var totalCount = 0
  var currentCount = 0
  var startTime = System.currentTimeMillis

  def receive = {
    // New count to add
    case i: Int ⇒ {
      totalCount += i
      currentCount += i
    }

    // Log it out - every 5s
    case ReportCount ⇒ {
      val endTime = System.currentTimeMillis
      val duration = endTime - startTime // ms

      if (currentCount > 0 && duration > 0) {
        val throughput = currentCount / (duration / 1000) // per second
        log.info(s"totalCount: $totalCount, throughput: $throughput /s")

        // Reset Counters
        currentCount = 0
        startTime = endTime
      }
    }
  }

  case object ReportCount

  // We need a dispatcher to run the code
  implicit val ec = context.dispatcher

  // Tell myself to log at an interval
  import scala.concurrent.duration._
  context.system.scheduler.schedule(5 seconds, 5 seconds) {
    self ! ReportCount
  }
}
