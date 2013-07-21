package kalmanb.akka

import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorRef
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import akka.actor.ActorLogging
import scala.concurrent.duration._
import com.typesafe.config.Config

object Master extends App {
  println("Master Starting ...")

  val system = ActorSystem("master")

  system.actorOf(Props(new Controller), name = "controller")
  system.actorOf(Props(new Counter), name = "counter")

//  system.actorOf(Props(new Worker))
//  system.actorOf(Props(new Worker))
}

case object ReadyForWork
case class Work(id: Int)

class Controller extends Actor {
  val workToDo = (1 to 100).to[ListBuffer]

  def receive = {
    case ReadyForWork ⇒ {
      if (workToDo.size > 0) {
        sender ! Work(workToDo.head)
        workToDo -= workToDo.head
      } else {
        println("Done")
      }
    }
  }
}

class Counter extends Actor with ActorLogging {
  var totalCount = 0
  var currentCount = 0
  var startTime = System.currentTimeMillis

  def receive = {
    case i: Int ⇒ {
      totalCount += i
      currentCount += i
    }
    case ReportCount ⇒ {
      val endTime = System.currentTimeMillis
      val duration = endTime - startTime // ms
      if (currentCount > 0 && duration > 0) {
        val throughput = currentCount / (duration / 1000) // per second
        currentCount = 0 // reset
        startTime = endTime // reset
        log.info(s"totalCount: $totalCount, throughput: $throughput /s")
      }
    }
  }
  case object ReportCount

  // We need a dispatcher to run the code
  implicit val ec = context.dispatcher

  // Tell myself to log at an interval
  context.system.scheduler.schedule(0 seconds, 5 seconds) {
    self ! ReportCount
  }
}

class Worker(baseUrl: String = "") extends Actor with ActorLogging {

  val controller = context.system.actorFor(baseUrl + "/user/controller")
  controller ! ReadyForWork

  val counter = context.system.actorFor(baseUrl + "/user/counter")

  def receive = {
    case i ⇒ {
      Common.blockingTask(1000)
      log.info(s"finished $i")
      controller ! ReadyForWork
      counter ! 1
    }
  }

}