package kalmanb.akka.push

import akka.actor.ActorSystem
import akka.actor.Props
import com.typesafe.config.ConfigFactory
import kalmanb.akka.Common
import kalmanb.akka.ConfigurablePort

object PushRemoteClient extends App with ConfigurablePort {
  println(s"PushRemoteClient1 Starting on port: $port ...")

  // Load config for client
  val customConf = ConfigFactory.parseString(s"""
    akka {
		  actor {
		    provider = "akka.remote.RemoteActorRefProvider"
		  }
		  remote {
		    enabled-transports = ["akka.remote.netty.tcp"]
		    netty.tcp {
		      hostname = "127.0.0.1"
		      port = $port
		    }
		 }
     loggers = ["akka.event.slf4j.Slf4jLogger"]
     loglevel = "DEBUG"
     logging-filter = "akka.event.slf4j.Slf4jLoggingFilter"
	  }
      """)
  val system = ActorSystem("client", (ConfigFactory.load(customConf)))

  Common.shutdown(system)
}

