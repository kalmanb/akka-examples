package kalmanb.akka

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import kalmanb.akka.test.TestSpec
import org.mockito.Matchers._
import kalmanb.akka.Common

class FirstTest extends TestSpec {
  import Common._

  // Default to 50 threads for execution
  implicit val ec = executors(50)

  describe("proceedural") {
    ignore("should work") {
      timedTask {
        lowCpuTask
      }
    }

    it("what is the max through put") {
      timedTask { // 52s
        implicit val ec = executors(1)
        threadedTask(10) {
          lowCpuTask
        }
      }
      timedTask { // 16s
        implicit val ec = executors(10)
        threadedTask(10) {
          lowCpuTask
        }
      }
      timedTask { // 16s
        implicit val ec = executors(50)
        threadedTask(10) {
          lowCpuTask
        }
      }
    }
  }



  def lowCpuTask = {
    blockingTask(1000) // DB read
    cpuTask(100)       // Computation
    blockingTask(1000) // Db write
  }

  def executors(numberOfThreads: Int) = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(numberOfThreads))




}
