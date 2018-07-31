package rolat.scala.test

import akka.actor.{Actor, Stash}
class ActorWithProtocol extends Actor with Stash {
  def receive = {
    case "open" ⇒
      unstashAll()
      context.become({
        case "write" ⇒ // do writing...
        case "close" ⇒
          unstashAll()
          context.unbecome()
        case msg ⇒ stash()
      }, discardOld = false) // stack on top instead of replacing
    case msg ⇒ stash()
  }
}