package kalmanb.akka.test

import org.mockito.Mockito
import org.mockito.verification.VerificationMode
import org.scalatest._
import org.scalatest.matchers._
import org.scalatest.mock.MockitoSugar

abstract class TestSpec extends FunSpec
  with ShouldMatchers
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
}
