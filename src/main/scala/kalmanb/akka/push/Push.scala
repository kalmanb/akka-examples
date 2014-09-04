package kalmanb.akka.push

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Status
import akka.actor.actorRef2Scala
import akka.routing.FromConfig
import akka.routing.RoundRobinRouter
import com.typesafe.config.ConfigFactory
import kalmanb.akka.Common

object Push extends App {
  println("Push Starting ...")

  val system = ActorSystem("master")

  val db = system.actorOf(Props(new Db))
  val processor = system.actorOf(Props(new Processor))
  val webserver = system.actorOf(Props(new WebServer(db, processor)))

  (1 to 100).foreach { i ⇒
    webserver ! WebServer.Request("/page/" + i)
  }

  Common.shutdown(system)

  /**
   *  Now we have a simple system, how do we scale up?
   *  Now how about out?
   */
}

object PushError extends App {
  println("PushError Starting ...")

  val system = ActorSystem("master")

  val db = system.actorOf(Props(new Db))
  val processor = system.actorOf(Props(new Error))
  val webserver = system.actorOf(Props(new WebServer(db, processor)))

  (1 to 1).foreach { i ⇒
    webserver ! WebServer.Request("/page/" + i)
  }

  Common.shutdown(system)

  // Define an Actor that throws exceptions
  class Error extends Actor {
    def receive = {
      case _ ⇒ {
        val e = new Exception("error")
        // Send it back to sender
        sender ! new Status.Failure(e)
      }
    }
  }
}

object PushRouted extends App {
  println("PushRouted Starting ...")

  val system = ActorSystem("master")

  val db = system.actorOf(Props(new Db).withRouter(RoundRobinRouter(nrOfInstances = 10)))
  val processor = system.actorOf(Props(new Processor).withRouter(RoundRobinRouter(nrOfInstances = 10)))
  val webserver = system.actorOf(Props(new WebServer(db, processor)))

  (1 to 100).foreach { i ⇒
    webserver ! WebServer.Request("/page/" + i)
  }

  /**
   * Some Router Types
   *
   * Round Robin
   * Random
   * Smallest MailBox
   * Broadcast
   * Scatter Gather First Completed
   * Consistent Hashing - eg cache distribution
   *
   * All Routers can be dynamically resized
   *
   * Dispatchers vs Actors
   */

  Common.shutdown(system)
}

object PushConfigured extends App {
  println("PushConfigured Starting ...")

  // Normally just put this in /application.conf
  val customConf = ConfigFactory.parseString("""
    akka {
      loggers = ["akka.event.slf4j.Slf4jLogger"]
      loglevel = "DEBUG"
      logging-filter = "akka.event.slf4j.Slf4jLoggingFilter"

      actor.deployment {
        /db {
          router = round-robin
          nr-of-instances = 10
        }
        /processor {
          router = round-robin
          nr-of-instances = 10
        }
      }
    }
      """)
  val system = ActorSystem("master", ConfigFactory.load(customConf))

  val db = system.actorOf(Props(new Db).withRouter(FromConfig()), "db")
  val processor = system.actorOf(Props(new Processor).withRouter(FromConfig()), "processor")
  val webserver = system.actorOf(Props(new WebServer(db, processor)))

  (1 to 100).foreach { i ⇒
    webserver ! WebServer.Request("/page/" + i)
  }

  Common.shutdown(system)
}

object PushDispatchers extends App {
  println("PushDispatchers Starting ...")

  // Normally just put this in /application.conf
  val customConf = ConfigFactory.parseString("""
    akka {
      loggers = ["akka.event.slf4j.Slf4jLogger"]
      loglevel = "DEBUG"
      logging-filter = "akka.event.slf4j.Slf4jLoggingFilter"

      actor.deployment {
        /db {
          router = round-robin
          nr-of-instances = 5

          // Config dispatcher
          dispatcher = db-dispatcher
        }
       /processor {
          router = round-robin
          nr-of-instances = 5
        }
      }
    }

      // Dispatchers
    db-dispatcher {
		  type = Dispatcher

		  # What kind of ExecutionService to use
		  executor = "fork-join-executor"

		  # Configuration for the fork join pool
		  fork-join-executor {
		    # Min number of threads to cap factor-based parallelism number to
		    parallelism-min = 2

		  	# Parallelism (threads) ... ceil(available processors * factor)
		    parallelism-factor = 2.0

		  	# Max number of threads to cap factor-based parallelism number to
		    parallelism-max = 1
		  }

		  # Throughput defines the maximum number of messages to be
		  # processed per actor before the thread jumps to the next actor.
		  # Set to 1 for as fair as possible.
		  throughput = 100
		}

       // Only override what you need
    db-router-dispatcher {
		  fork-join-executor {
		    parallelism-max = 2
		  }
		  throughput = 10
		}
      """)
  val system = ActorSystem("master", ConfigFactory.load(customConf))

  val db = system.actorOf(Props(new Db).withRouter(FromConfig()).withDispatcher("db-router-dispatcher"), name = "db")
  val processor = system.actorOf(Props(new Processor).withRouter(FromConfig()), "processor")
  val webserver = system.actorOf(Props(new WebServer(db, processor)))

  (1 to 100).foreach { i ⇒
    webserver ! WebServer.Request("/page/" + i)
  }

  Common.shutdown(system)
}

object PushRemoteMaster extends App {
  println("PushRemoteMaster Starting ...")

  val customConf = ConfigFactory.parseString("""
    akka {
      loggers = ["akka.event.slf4j.Slf4jLogger"]
      loglevel = "DEBUG"
      logging-filter = "akka.event.slf4j.Slf4jLoggingFilter"
      remote {
    	  enabled-transports = ["akka.remote.netty.tcp"]
    	  netty.tcp {
    	    port = 2552
    	  }
    	}
      actor {
  		  provider = "akka.remote.RemoteActorRefProvider"
  
            deployment {
  	        /db {
  	            remote = "akka.tcp://client@127.0.0.1:2553"
  		      }
  	        /processor {
  	            remote = "akka.tcp://client@127.0.0.1:2553"
  		      }
          }
        }
      }
      """)
  val system = ActorSystem("master", ConfigFactory.load(customConf))

  val db = system.actorOf(Props(new Db), "db")
  val processor = system.actorOf(Props(new Processor), "processor")
  val webserver = system.actorOf(Props(new WebServer(db, processor)))

  (1 to 100) foreach { i ⇒
    webserver ! WebServer.Request("/page/" + i)
  }

  Common.shutdown(system)
}

object PushDistributedMaster extends App {
  println("PushDistributedMaster Starting ...")

  val customConf = ConfigFactory.parseString("""
    akka {
      loggers = ["akka.event.slf4j.Slf4jLogger"]
      loglevel = "DEBUG"
      logging-filter = "akka.event.slf4j.Slf4jLoggingFilter"
      remote {
	  	  enabled-transports = ["akka.remote.netty.tcp"]
	  	  netty.tcp {
	  	    port = 2552
	  	  }
	  	}
      actor {
	  	  provider = "akka.remote.RemoteActorRefProvider"
            deployment {
	          // This configuration setting will clone the actor “processor” 10 times
	          // and deploy it evenly distributed across the two given target nodes.

	          /db {
	  	        router = "round-robin"
	  	        nr-of-instances = 10
	  	         target {
	  	           nodes = ["akka.tcp://client@127.0.0.1:2553","akka.tcp://client@127.0.0.1:2554"]
	  	        }
	  	      }

	          /processor {
	  	        router = "round-robin"
	  	        nr-of-instances = 10
	  	         target {
	  	           nodes = ["akka.tcp://client@127.0.0.1:2553","akka.tcp://client@127.0.0.1:2554"]
	  	        }
	  	      }
          }
        }
      }
      """)
  val system = ActorSystem("master", ConfigFactory.load(customConf))

  val db = system.actorOf(Props(new Db).withRouter(FromConfig()), "db")
  val processor = system.actorOf(Props(new Processor).withRouter(FromConfig()), "processor")
  val webserver = system.actorOf(Props(new WebServer(db, processor)))

  (1 to 100).foreach { i ⇒
    webserver ! WebServer.Request("/page/" + i)
  }

  Common.shutdown(system)
}
