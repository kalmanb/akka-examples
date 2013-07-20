package kalmanb.akka

import kalmanb.akka.test.TestSpec
import org.mockito.Matchers._

class FirstTest extends TestSpec {
  import Common._

  describe("a first test") {
    it("should work") {
      cpuTask(100)
    }
  }




}
