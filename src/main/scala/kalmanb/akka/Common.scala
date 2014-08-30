package kalmanb.akka

import akka.actor.ActorSystem

object Common {

  def shutdown(system: ActorSystem): Unit = {
    readLine()
    system.shutdown
    System.exit(0)
  }

}

trait ConfigurablePort {
  print("Select port to run on [2553]:")
  val input = readLine()
  val port = if (input.isEmpty) "2553" else input
}

