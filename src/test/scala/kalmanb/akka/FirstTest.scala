package kalmanb.akka

import java.util.concurrent.Executors 
import scala.concurrent.ExecutionContext

import kalmanb.akka.test.TestSpec
import org.mockito.Matchers._

class FirstTest extends TestSpec {
  import Common._

  describe("proceedural") {
    ignore("should work") {
      timedTask {
        blockingTask(1000)
        cpuTask(100)
        blockingTask(1000)
      }
    }

    it("what is the max through put") {
      implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(50))
      threadedTask(10) {
        println("kanL")
        blockingTask(4000)
      }
    }
  }




}
