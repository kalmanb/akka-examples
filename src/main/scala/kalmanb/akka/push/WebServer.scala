package kalmanb.akka.push

import akka.actor.ActorLogging
import akka.util.Timeout
import akka.actor.Actor
import akka.actor.ActorRef
import akka.pattern._
import scala.util.Failure
import scala.util.Success

object WebServer {
  case class Request(url: String)
}
class WebServer(db: ActorRef, processor: ActorRef) extends Actor with ActorLogging {
  import WebServer._
  import scala.concurrent.duration._

  implicit val timeout = Timeout(5 minutes)

  // We're also going to need somewhere to execute the future
  // We'll just use this Actors ExecutionContext
  implicit val exec = context.dispatcher

  def receive = {
    case Request(url) ⇒ {
      log.info(s"Processing $url")

      // We need to read, process and write to DB, then return updated
      val result = for {
        read ← db ? Db.Read(s"select * from $url")
        processed ← processor ? read
        updated ← db ? Db.Write(processed.asInstanceOf[String]) // Oh no Actor messages arn't typed
      } yield updated.asInstanceOf[String]


      // Now return to user, success or failure
      result.onComplete {
        case Success(result) ⇒ {
          // Return 200 OK to user with results
          log.info(s"done - $result")
        }
        case Failure(failure) ⇒ log.error(s"Oh no!, $failure")
      }
    }
  }
}
