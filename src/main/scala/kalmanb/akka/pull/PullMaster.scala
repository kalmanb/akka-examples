package kalmanb.akka.pull

import akka.actor.ActorSystem
import kalmanb.akka.Common
import akka.actor.Props
import com.typesafe.config.ConfigFactory

object PullMaster extends App {
  println("PullMaster Starting ...")

  val customConf = ConfigFactory.parseString(s"""
   akka {
		 actor {
		    provider = "akka.remote.RemoteActorRefProvider"
		 }
		 remote {
		    enabled-transports = ["akka.remote.netty.tcp"]
		    netty.tcp {
          hostname = "127.0.0.1"
		      port = 2552
		    }
		 }
     loggers = ["akka.event.slf4j.Slf4jLogger"]
     loglevel = "DEBUG"
     logging-filter = "akka.event.slf4j.Slf4jLoggingFilter"
	  }
      """)
  val system = ActorSystem("master", ConfigFactory.load(customConf))

  val controller = system.actorOf(Props(new Controller), name = "controller")
  val counter = system.actorOf(Props(new Counter), name = "counter")

  // Start workers - just one for now
  (1 to 1).foreach { i ⇒
    system.actorOf(Props(new Worker(controller, counter)))
  }

  Common.shutdown(system)
}
