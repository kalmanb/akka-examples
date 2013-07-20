package kalmanb.akka

import scala.collection.JavaConversions._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration._

import akka.dispatch.Futures

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

  def timedTask[T](f: => T):T = {
    val start = System.currentTimeMillis
    val result = f
    val end = System.currentTimeMillis
    println(s"Duration: ${end - start} millis")
    result
  }                          

  def threadedTask(numberOfThreads: Int)(f: => Any)(implicit context: ExecutionContext):Unit = {
    val results = for(i <- 1 until numberOfThreads) yield {
      Future(f)
    }
    val result = Futures.sequence(results, context)
    Await.result(result, 100 seconds)
  }

}


