package rolat.scala.sample.camel

import akka.Main.Terminator
import akka.actor.{Actor, ActorSystem, ExtendedActorSystem, Props}

import scala.util.control.NonFatal

object Main {

  def main(args: Array[String]): Unit = {
    println(classOf[HelloWorld].getName)
   /* akka.Main.main(Array(classOf[HelloWorld].getName))*/

    val system = ActorSystem("Main")
    try {
      val appClass = system.asInstanceOf[ExtendedActorSystem].dynamicAccess.getClassFor[Actor](classOf[HelloWorld].getName).get
      println(appClass)
      val app = system.actorOf(Props(appClass), "app")
      val terminator = system.actorOf(Props(classOf[Terminator], app), "app-terminator")
    } catch {
      case NonFatal(e) ⇒ system.terminate(); throw e
    }
  }

}