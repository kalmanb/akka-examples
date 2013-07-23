package kalmanb.akka.push

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import kalmanb.akka.Common
import akka.actor.Props

object PushRemoteClient1 extends App {
  println("PushRemoteClient1 Starting ...")

  // Load config for client
  val customConf = ConfigFactory.parseString("""
      akka {
		  actor {
		    provider = "akka.remote.RemoteActorRefProvider"
		  }
		  remote {
		    enabled-transports = ["akka.remote.netty.tcp"]
		    netty.tcp {
		      hostname = "127.0.0.1"
		      port = 2553
		    }
		 }
	  }
      """)
  val system = ActorSystem("client", ConfigFactory.load(customConf))

  Common.shutdown(system)
}

object PushRemoteClient2 extends App {
  println("PushRemoteClient2 Starting ...")

  // Load config for client
  val customConf = ConfigFactory.parseString("""
      akka {
		  actor {
		    provider = "akka.remote.RemoteActorRefProvider"
		  }
		  remote {
		    enabled-transports = ["akka.remote.netty.tcp"]
		    netty.tcp {
		      hostname = "127.0.0.1"
		      port = 2554
		    }
		 }
	  }
      """)
  val system = ActorSystem("client", ConfigFactory.load(customConf))

  Common.shutdown(system)
}