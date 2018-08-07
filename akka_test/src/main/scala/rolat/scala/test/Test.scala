package rolat.scala.test

import scala.concurrent.duration.Duration

object Test extends App {
    val x=Vector.fill(10)("a").mkString
    println(x)
    println(1)
    val start1=System.nanoTime
    val start2=System.currentTimeMillis()
    Thread.sleep(1000)
    println((System.nanoTime-start1)/(1000*1000))
    println(System.currentTimeMillis()-start2)
    println(Duration.Undefined)
}
