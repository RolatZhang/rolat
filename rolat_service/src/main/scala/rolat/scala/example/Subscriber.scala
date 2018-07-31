package rolat.scala.example

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe, SubscribeAck}

class Subscriber extends Actor with ActorLogging {
  import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
  val mediator = DistributedPubSub(context.system).mediator
  // subscribe to the topic named "content"
  mediator ! Subscribe("content", self)

  def receive = {
    case s: String ⇒
      log.info("Got {}", s)
    case SubscribeAck(Subscribe("content", None, `self`)) ⇒
      log.info("subscribing")
  }


}

object Main extends App {
  val system=ActorSystem()
    system.actorOf(Props[Subscriber], "subscriber1")
    /*system.actorOf(Props[Subscriber], "subscriber2")
    system.actorOf(Props[Subscriber], "subscriber3")*/


  val publisherActor=system.actorOf(Props[Publisher], "Publisher")
  (1 until 10).map(x=>{
    println(x)
    publisherActor ! x+"内容"
  })
}

class Publisher extends Actor {
  import akka.cluster.pubsub.DistributedPubSubMediator.Publish
  // activate the extension
  val mediator = DistributedPubSub(context.system).mediator

  def receive = {
    case in: String ⇒
      val out = in.toUpperCase
      mediator ! Publish("content", out)
  }
}