package kalmanb.akka

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.event.ActorEventBus
import akka.event.LookupClassification

case class MessageEvent(val channel: String, val message: String)

class AppActorEventBus extends ActorEventBus with LookupClassification {
  type Event = MessageEvent
  type Classifier = String

  protected def mapSize(): Int = 10

  protected def classify(event: Event): Classifier = {
    event.channel
  }

  protected def publish(event: Event, subscriber: Subscriber): Unit = {
    subscriber ! event
  }
}

object EventBus extends App {
  val system = ActorSystem("MySystem")
  val appActorEventBus = new AppActorEventBus

  val subscriber = system.actorOf(Props(new Actor {
    def receive = {
      case event: MessageEvent ⇒ println(event.message)
    }
  }))

  appActorEventBus.subscribe(subscriber, "/posts/new")
  Thread sleep 2000
  println("Publish Event")
  appActorEventBus.publish(MessageEvent("/posts/new", "hello world"))

  Common.shutdown(system)
}
