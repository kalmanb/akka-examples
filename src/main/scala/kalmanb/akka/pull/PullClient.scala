package kalmanb.akka.pull

import akka.actor.ActorSystem
import akka.actor.Props
import com.typesafe.config.ConfigFactory
import kalmanb.akka.Common
import kalmanb.akka.ConfigurablePort 

object PullClient extends App with ConfigurablePort {
  val RemoteUrl = "akka.tcp://master@127.0.0.1:2552"

  println(s"PullClient Starting on port: $port ...")

  val customConf = ConfigFactory.parseString(s"""
      akka {
		  actor {
		    provider = "akka.remote.RemoteActorRefProvider"
		  }
		  remote {
		    enabled-transports = ["akka.remote.netty.tcp"]
		    netty.tcp {
		      port = $port
		    }
		 }
	  }
      """)
  val system = ActorSystem("client", ConfigFactory.load().withFallback(ConfigFactory.load(customConf)))

  // Note "actorFor" is a lookup - not creation
  val controller = system.actorFor(RemoteUrl + "/user/controller")
  val counter = system.actorFor(RemoteUrl + "/user/counter")

  // Start 10 workers
  (1 to 10).foreach { i ⇒
    system.actorOf(Props(new Worker(controller, counter)))
  }

  Common.shutdown(system)
}

