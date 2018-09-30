package rolat.service.scala.worker

import java.util.Properties

import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}
import akka.cluster.Cluster
import akka.routing.{FromConfig, RoundRobinPool}
import com.sgcc.sgd5000.hisdata.HisViewAcq
import com.sgcc.sgd5000.meas.Meters
import com.typesafe.config.ConfigFactory
import rolat.service.scala.backend.MetricsListener
import rolat.service.scala.message.Messages.{RequestMsg, ResponseMsg, Result}

import scala.concurrent.duration._


object WorkActor {
  def propsWorkActor(workNum: Int,dbActorRef:ActorRef) = Props(new WorkActor(workNum,dbActorRef))
  def propsWorkActorSuper(workNum: Int) = Props(new WorkActorSuper(workNum))
}
/**
  * 计算支撑
  */
class WorkActor(workNum: Int,dbActorRef:ActorRef) extends Actor{

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println(s"Restarting calculator: ${reason.getMessage}")
    super.preRestart(reason, message)
  }
  val hisViewAcqRouter: ActorRef =
    context.actorOf(
      Props.create(classOf[HisViewAcqActor]).
        withDispatcher("work-dispatcher").
        withRouter(new RoundRobinPool(workNum)), "hisViewAcqActor")

  val cluster = Cluster(context.system)

  override def receive: Receive = {
    case requestMsg:RequestMsg =>{
      requestMsg.id match {
        case "processHisViewAcq" =>{
          dbActorRef ! RequestMsg("getHisViewAcqList",requestMsg.request)
        }
      }
    }
    case responseMsg:ResponseMsg =>{
      responseMsg.id match {
        case "getHisViewAcqList" =>{
          hisViewAcqRouter ! RequestMsg("processHisViewAcqList",responseMsg.response)
        }
        case "processHisViewAcqList" =>{
          dbActorRef ! ResponseMsg("saveHisViewAcqList",responseMsg.response)
        }
      }

    }
  }
  def processMeters(meters:Meters): Unit ={
    println(s"${self}收到===========${meters.getMeterName}")
        sender() ! Result(1,"成功收到了"+meters.getMeterName+"耗时",System.currentTimeMillis())

  }
  def process(hisViewAcq:HisViewAcq): Unit ={

  }
}

class WorkActorSuper(workNum: Int) extends Actor {
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


  val dbActorRouter = context.actorOf(FromConfig.props(Props.empty), name = "dbActorRouter")


  val workActor: ActorRef =
    context.system.actorOf(
      WorkActor.propsWorkActor(workNum,dbActorRouter).
        withDispatcher("work-dispatcher").
        withRouter(new RoundRobinPool(workNum)), "workActor")


  override def receive: Receive = {
    case msg@ _ => workActor.forward(msg)
  }

}

/**
  * 创建后台
  */
object WorkActorer {
  val prop: Properties = new Properties
  val inputStream = WorkActorer.getClass.getClassLoader.getResourceAsStream("init.properties")
  prop.load(inputStream)
  val num:String =prop.getProperty("worker.num")


  def create(port: Int): Unit = {   //create instances of backend Calculator
    val config = ConfigFactory.parseString("akka.cluster.roles = [backend]")
      .withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port"))
      .withFallback(ConfigFactory.load("worker"))
    val calcSystem = ActorSystem("calcClusterSystem",config)


    calcSystem.actorOf(WorkActor.propsWorkActorSuper(num.toInt), "workActorSuper")
    /*val workers=calcSystem.actorOf(WorkActor.propsWorkActorSuper.withDispatcher("work-dispatcher").
      withRouter(new BalancingPool(num.toInt)), "workActorer")*/





    val monitorFlag=prop.getProperty("monitor.service")
    monitorFlag match {
      case "true" => calcSystem.actorOf(Props[MetricsListener], name = "metricsListener")
      case msg@_ => println(msg)
    }








  }
}

