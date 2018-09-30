package rolat.scala.message

import akka.actor._

import rolat.scala.message.Messages._


import scala.concurrent.duration._

import com.typesafe.config.ConfigFactory


object CalcFuctions {
  def propsFuncs = Props(new CalcFuctions)
  def propsSuper = Props(new CalculatorSupervisor)
}

class CalcFuctions extends Actor {
  override def receive: Receive = {
    case Add(x,y) =>
      println(s"$x + $y carried out by ${self} with result=${x+y}")
    case Sub(x,y) =>
      println(s"$x - $y carried out by ${self} with result=${x - y}")
    case Mul(x,y) =>
      println(s"$x * $y carried out by ${self} with result=${x * y}")
    case Div(x,y) =>
      println(s"$x / $y carried out by ${self} with result=${x / y}")

    case HisViewAcq =>
      println("计算hisview")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println(s"Restarting calculator: ${reason.getMessage}")
    super.preRestart(reason, message)
  }
}

/**
  * 后台计算总的支撑
  */
class CalculatorSupervisor extends Actor {
  def decider: PartialFunction[Throwable,SupervisorStrategy.Directive] = {
    case _: NullPointerException => SupervisorStrategy.Resume
  }

  /**
    * 监督策略（5秒内只允许重启多少次）
    * @return
    */
  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 5, withinTimeRange = 5 seconds){
      decider.orElse(SupervisorStrategy.defaultDecider)
    }

  val calcActor = context.actorOf(CalcFuctions.propsFuncs,"calcFunction")

  override def receive: Receive = {
    case msg@ _ => calcActor.forward(msg)
  }

}


object Calculator {
  def create(port: Int): Unit = {   //create instances of backend Calculator
    val config = ConfigFactory.parseString("akka.cluster.roles = [backend]")
      .withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port"))
      .withFallback(ConfigFactory.load())
    val calcSystem = ActorSystem("calcClusterSystem",config)
    val calcRef = calcSystem.actorOf(CalcFuctions.propsSuper,"calculator")
  }
}