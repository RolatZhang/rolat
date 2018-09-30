package rolat.scala.calTest

import akka.actor._
import rolat.scala.calTest.Messages._
import com.typesafe.config.ConfigFactory


object CalcRouter {
  def props = Props(new CalcRouter)
}

/**
  * 所有启动的后台注册到这里来
  */
class CalcRouter extends Actor {
  var nodes: Map[String,ActorRef] = Map()
  override def receive: Receive = {
    case RegisterBackendActor(role) =>
      nodes += (role -> sender())
      context.watch(sender())
    case add: Add => routeCommand("adder", add)
    case sub: Sub => routeCommand("substractor",sub)
    case mul: Mul => routeCommand("multiplier",mul)
    case div: Div => routeCommand("divider",div)

    case Terminated(ref) =>    //remove from register
      nodes = nodes.filter { case (_,r) => r != ref}

  }
  def routeCommand(role: String, ops: MathOps): Unit = {
    nodes.get(role) match {
      case Some(ref) => ref ! ops
      case None =>
        println(s"$role not registered!")
    }
  }
}

object FrontEnd {
  private var router: ActorRef = _
  def create = {  //must load this seed-node before any backends
    val calcSystem = ActorSystem("calcClusterSystem",ConfigFactory.load().getConfig("Frontend"))
    router = calcSystem.actorOf(CalcRouter.props,"frontend")
  }
  def getRouter = router
}