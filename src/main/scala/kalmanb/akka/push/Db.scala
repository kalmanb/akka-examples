package kalmanb.akka.push

import akka.actor.ActorLogging
import akka.actor.Actor

object Db {
  case class Read(data: String)
  case class ReadResult(data: String)
  case class Write(data: String)
}

class Db extends Actor with ActorLogging {
  import Db._

  def receive = {
    case Read(data) ⇒ {
      // Working
      Thread sleep 100
      if (true)
        sender ! ReadResult(s"heres: $data")
      else
        throw new Exception
    }

    case Write(data) ⇒ {
      // Working
      Thread sleep 100
      if (true)
        sender ! s"Updated : $data"
      else
        throw new Exception("oh no")
    }
  }
}
