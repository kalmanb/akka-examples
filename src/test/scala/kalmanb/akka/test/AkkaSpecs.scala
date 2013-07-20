package kalmanb.akka.test

import java.util.UUID
import scala.annotation.tailrec
import scala.collection.mutable
import scala.concurrent._
import scala.concurrent.duration._
import scala.reflect.ClassTag

import akka.actor._
import akka.testkit._
import akka.util.Timeout

abstract class AkkaSpec(failForDeadletter: Boolean = true, failForUnhandledMessages: Boolean = true) extends SimpleAkkaSpec {
  val listener = system.actorOf(Props(new Actor {
    def receive = {
      case m: DeadLetter if failForDeadletter ⇒
        //TODO: failing this doesn't work as it runs in a separate thread!
        fail("Received a dead letter: " + m)
      case m: UnhandledMessage if failForUnhandledMessages ⇒
        fail("Some message wasn't delivered: check that your actor's receive methods handle all messages you need: " + m)
    }
  }))

  system.eventStream.subscribe(listener, classOf[DeadLetter])
  system.eventStream.subscribe(listener, classOf[UnhandledMessage])
}

abstract class SimpleAkkaSpec extends AkkaSpecBase {
  // this must be lazy because TestKitBase requires it to be available BEFORE the ctor is run
  implicit lazy val system = ActorSystem("AkkaTestSystem")
}

trait AkkaSpecBase extends TestSpec with TestKitBase {

  implicit val system: ActorSystem // must be implemented with a lazy val!
  implicit def self = testActor // proxy for ImplicitSender, which self-types to TestKit (not TestKitBase)
  implicit val timeout = Timeout(10 seconds)

  override def afterAll {
    system.shutdown
  }

  def randomUuid = UUID.randomUUID.toString

  def expectMsgAllOfIgnoreOthers[T: ClassTag](max: Duration, expected: T*) {
    val outstanding = mutable.Set(expected: _*)
    fishForMessage(max) {
      case msg: T if outstanding contains msg ⇒
        outstanding remove msg
        outstanding.isEmpty
      case _ ⇒ false
    }
  }

  def expectMsgAllOfIgnoreOthers[T: ClassTag](expected: T*) {
    expectMsgAllOfIgnoreOthers(3 seconds, expected: _*)
  }

  def ignoreMsgs[T](fun: ⇒ T) = {
    ignoreMsg { case _ ⇒ true }
    val result = fun
    ignoreNoMsg
    result
  }

  def awaitCondition[T](message: String, max: Duration = 10 seconds, interval: Duration = 10 millis)(predicate: ⇒ T) {
    val stop = now + max

    @tailrec
    def poll(nextSleepInterval: Duration) {
      try predicate
      catch {
        case e: Throwable ⇒
          if (now > stop)
            throw e
          else {
            Thread.sleep(nextSleepInterval.toMillis)
            poll((stop - now) min interval)
          }
      }
    }

    poll(max min interval)
  }

  def waitFor[T: ClassTag](fun: Future[T]) = Await.result(fun, timeout.duration)
  def waitForAll[T: ClassTag](funs: Seq[Future[T]]) = funs.map(Await.ready(_, timeout.duration))

}
