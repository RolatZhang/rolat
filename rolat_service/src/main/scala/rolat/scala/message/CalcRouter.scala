package rolat.scala.message

import akka.actor._
import akka.routing.FromConfig
import com.sgcc.sgd5000.meas.Meters
import com.typesafe.config.ConfigFactory

object CalcRouter {
  def props = Props(new CalcRouter)
}

class CalcRouter extends Actor {

  // This router is used both with lookup and deploy of routees. If you
  // have a router with only lookup of routees you can use Props.empty
  // instead of Props[CalculatorSupervisor].
  val calcRouter = context.actorOf(FromConfig.props(Props.empty),
    name = "calcRouter")

  override def receive: Receive = {
    case meters:Meters => println(meters.getMeterName)
    case msg@ _ => {
      println(msg+"未匹配")
      calcRouter forward msg
    }
  }
}

object FrontEnd {
  private var router: ActorRef = _
  def create = {  //must load this seed-node before any backends
    val calcSystem = ActorSystem("calcClusterSystem",ConfigFactory.load("hashing"))
    router = calcSystem.actorOf(CalcRouter.props,"frontend")
  }
  def getRouter = router
}






