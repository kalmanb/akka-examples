package kalmanb.akka.pull

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import kalmanb.akka.Common
import akka.actor.Props

object PullClient extends App {
  val RemoteUrl = "akka.tcp://master@127.0.0.1:2552"

  println("PullClient Starting ...")

  val customConf = ConfigFactory.parseString("""
      akka {
		  actor {
		    provider = "akka.remote.RemoteActorRefProvider"
		  }
		  remote {
		    enabled-transports = ["akka.remote.netty.tcp"]
		    netty.tcp {
		      port = 2553
		    }
		 }
	  }
      """)
  val system = ActorSystem("client", ConfigFactory.load(customConf))

  // Note "actorFor" is a lookup - not creation
  val controller = system.actorFor(RemoteUrl + "/user/controller")
  val counter = system.actorFor(RemoteUrl + "/user/counter")

  // Start 10 workers
  (1 to 10).foreach { i ⇒
    system.actorOf(Props(new Worker(controller, counter)))
  }

  Common.shutdown(system)
}

