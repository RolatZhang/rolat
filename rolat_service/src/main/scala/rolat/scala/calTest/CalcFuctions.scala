package rolat.scala.calTest

import akka.actor._

import scala.concurrent.duration._
import akka.cluster._
import akka.cluster.ClusterEvent._
import com.typesafe.config.ConfigFactory
import rolat.scala.calTest.Messages._


object CalcFuctions {
  def propsFuncs = Props(new CalcFuctions)
  def propsSuper(role: String) = Props(new CalculatorSupervisor(role))
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
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println(s"Restarting calculator: ${reason.getMessage}")
    super.preRestart(reason, message)
  }
}

class CalculatorSupervisor(mathOps: String) extends Actor {
  def decider: PartialFunction[Throwable,SupervisorStrategy.Directive] = {
    case _: ArithmeticException => SupervisorStrategy.Resume
  }

  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 5, withinTimeRange = 5 seconds){
      decider.orElse(SupervisorStrategy.defaultDecider)
    }

  val calcActor = context.actorOf(CalcFuctions.propsFuncs,"calcFunction")
  val cluster = Cluster(context.system)
  override def preStart(): Unit = {
    cluster.subscribe(self,classOf[MemberUp])
    super.preStart()
  }

  override def postStop(): Unit =
    cluster.unsubscribe(self)
    super.postStop()

  override def receive: Receive = {
    case MemberUp(m) =>
      if (m.hasRole("frontend")) {
        context.actorSelection(RootActorPath(m.address)+"/user/frontend") !
          RegisterBackendActor(mathOps)
      }
    case msg@ _ => calcActor.forward(msg)
  }

}

object Calculator {
  def create(role: String): Unit = {   //create instances of backend Calculator
    val config = ConfigFactory.parseString("Backend.akka.cluster.roles = [\""+role+"\"]")
      .withFallback(ConfigFactory.load()).getConfig("Backend")
    val calcSystem = ActorSystem("calcClusterSystem",config)
    val calcRef = calcSystem.actorOf(CalcFuctions.propsSuper(role),"calculator")
  }
}