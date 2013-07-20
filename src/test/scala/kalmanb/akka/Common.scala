package kalmanb.akka

object Common {

  def blockingTask(duration: Int):String = {
    Thread sleep duration
    "Done"
  }

  def cpuTask(load: Int): String = {
    val n = 10000
    def rec(i: Int, primes: List[Int]): List[Int] = {
      if (i >= n) primes
      else if (prime(i, primes)) rec(i + 1, i :: primes)
      else rec(i + 1, primes)
    }
    def prime(num: Int, factors: List[Int]): Boolean = factors.forall(num % _ != 0)

    for (i <- 1 to load) {
      rec(2, List())
    }
    "CPU Done"
  }

}


