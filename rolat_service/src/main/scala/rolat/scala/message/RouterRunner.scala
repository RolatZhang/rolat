package rolat.scala.message


import akka.actor._
import akka.cluster._
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.util.Random
import com.dlptech.swap.tools.SpringUtils
import javax.annotation.Resource
import org.springframework.context.support.ClassPathXmlApplicationContext
import rolat.repository.controller.ServiceHolder
import rolat.repository.service.IMetersService
import rolat.scala.message.Messages._

import scala.collection.JavaConverters._

class RouterRunner extends Actor {
  val jobs = List(Add,Sub,Mul,Div)
  import context.dispatcher

  val calcRouter = context.actorOf(CalcRouter.props,"adaptive")
  context.system.scheduler.scheduleOnce( 3.seconds, self, "sendJobs")
  override def receive: Receive = {
    case "sendJobs" => println("sendJobs")
    case  _ => sendJobs()/*calcRouter ! anyMathJob*/
  }


  private var serviceHolder: ServiceHolder =_

  override def postRestart(reason: Throwable): Unit = ()

  override def preStart(): Unit ={
    val springContext = new ClassPathXmlApplicationContext("./applicationContext.xml")
    SpringUtils.installApplicationContext(springContext)
    serviceHolder=springContext.getBean("serviceHolder").asInstanceOf[ServiceHolder]
  }

  def sendJobs(): Unit ={
    val metersList =serviceHolder.metersService.getMetersList
    var i=0
    for (meters <- metersList.asScala) {
      i=i+1
      meters.setMeterName(meters.getMeterName+i)
      println("发送"+i+"||"+meters.getMeterName)
      calcRouter ! meters
    }
  }
  def anyMathJob: MathOps =
    jobs(Random.nextInt(4))(Random.nextInt(100), Random.nextInt(100))
}

object AdaptiveRouterDemo extends App {

  Calculator.create(2551)   //seed-node
  Calculator.create(0)      //backend node

  Thread.sleep(2000)


  val config = ConfigFactory.parseString("akka.cluster.roles = [frontend]").
    withFallback(ConfigFactory.load("adaptive"))

  val calcSystem = ActorSystem("calcClusterSystem",config)

  //#registerOnUp
  Cluster(calcSystem) registerOnMemberUp {
    val _ = calcSystem.actorOf(Props[RouterRunner],"frontend")
  }
  //#registerOnUp

  //val _ = calcSystem.actorOf(Props[RouterRunner],"frontend")

}
