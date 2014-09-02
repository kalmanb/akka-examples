package kalmanb.akka

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout

case class Person(name: String)

class BlockingExample extends Actor {

  def receive = {
    case 'Request ⇒
      Try {
        DB.selectQuery("select * from person;")
      } match {
        case Success(person) ⇒ println(s"Person is $person")
        case Failure(e)      ⇒ println(s"oh no: $e")
      }
      context stop self
  }
}
object DB {
  def selectQuery(sql: String): Person = ???
}

class AskExample(otherActor: ActorRef) extends Actor {
  implicit val dispatcher = context.dispatcher
  implicit val timeout = Timeout(5 seconds)

  def receive = {
    case 'Request ⇒
      val result: Future[Person] = (otherActor ? "select * from person;").mapTo[Person]
      result.onComplete {
        case Success(person) ⇒ println(s"Person is $person")
        case Failure(e)      ⇒ println(s"oh no: $e")
      }
      context stop self
  }
}

class TellExample(otherActor: ActorRef) extends Actor {
  implicit val dispatcher = context.dispatcher

  def receive = {
    case 'Request ⇒
      otherActor ! "select * from person;"
      context become waitingResponse
      // Schedule timeout
      context.system.scheduler.scheduleOnce(5 seconds, self, new Exception("timed out"))
  }

  def waitingResponse: Receive = {
    case person: Person ⇒
      println(s"Person is $person")
      context stop self
    case e ⇒
      println(s"oh no: $e")
      context stop self
  }
}

