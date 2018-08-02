
package rolat.scala.example

import java.util.concurrent.atomic.AtomicInteger

object Test extends App {
  val counter = new AtomicInteger
   (0 to 100).map(x=>{
     val a=counter.incrementAndGet()
     println(a+"||"+x)
   })

  val hello = new Thread(new Runnable {
    override def run(): Unit ={
      println("hello world")
    }
  })

}

