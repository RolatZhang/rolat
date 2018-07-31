package rolat.scala.test

import akka.actor.{ Actor, Props, ActorSystem }
import scala.io.StdIn

class PrintMyActorRefActor extends Actor {
  override def receive: Receive = {
    case "printit" ⇒
      val secondRef = context.actorOf(Props.empty, "second-actor")
      println(s"Second: $secondRef")
    case "printit2" ⇒
      val secondRef2 = context.actorOf(Props.empty, "second-actor2")
      println(s"Second2: $secondRef2")
  }
}

object ActorHierarchyExperiments extends App {
  val system = ActorSystem("testSystem")

  val firstRef = system.actorOf(Props[PrintMyActorRefActor], "first-actor")
  println(s"First: $firstRef")
  firstRef ! "printit"

  println(">>> Press ENTER to exit <<<")
  firstRef ! "printit2"
  try{
    val context=StdIn.readLine()
    println(context)

  }
  finally system.terminate()
}