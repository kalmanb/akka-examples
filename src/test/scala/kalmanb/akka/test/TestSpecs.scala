package kalmanb.akka.test

import org.mockito.Mockito
import org.mockito.ArgumentCaptor
import org.mockito.verification.VerificationMode
import org.scalatest._
import org.scalatest.matchers._
import org.scalatest.mock.MockitoSugar
import akka.testkit._
import akka.actor._
import reflect.ClassTag
import scala.concurrent.duration.Duration
import scala.concurrent.duration._
import scala.annotation.tailrec

trait TestSpec extends FunSpecLike
  with Matchers
  with MockitoSugar
  with MockitoWrapper
  with BeforeAndAfter
  with BeforeAndAfterEach
  with BeforeAndAfterAll

trait MockitoWrapper {
  def spy[T](mock: T) = Mockito.spy(mock)
  def verify[T](mock: T) = Mockito.verify(mock)
  def verify[T](mock: T, mode: VerificationMode) = Mockito.verify(mock, mode)
  def when[T](methodCall: T) = Mockito.when(methodCall)
  def never = Mockito.never
  def times(wantedNumberOfInvocations: Int) = Mockito.times(wantedNumberOfInvocations)
  def reset[T](mock: T) = Mockito.reset(mock)
  def doThrow(t: Throwable) = Mockito.doThrow(t)
  def argumentCaptor[T: ClassTag] = ArgumentCaptor.forClass(implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]])
  def verifyNoMoreInteractions[T <: AnyRef](mock: T) = Mockito.verifyNoMoreInteractions(mock)
}

trait AkkaSpec extends TestKitBase with TestSpec {
  // this must be lazy because TestKitBase requires it to be available BEFORE the actor is run
  implicit lazy val system = ActorSystem("AkkaTestSystem")

  def awaitCondition[T](message: String, max: Duration = 10 seconds, interval: Duration = 10 millis)(predicate: ⇒ T) {
    val stop = now + max

    @tailrec
    def poll(nextSleepInterval: Duration) {
      try predicate
      catch {
        case e: Throwable ⇒
          println("aaaaaaaaaa")
          if (now > stop)
            throw e
          else {
            Thread.sleep(nextSleepInterval.toMillis)
            poll((stop - now) min interval)
          }
      }
    }
  }

}
