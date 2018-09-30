package rolat.service.scala.master
import java.util
import java.util.{List, Properties}

import akka.actor._
import akka.cluster._
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
import akka.routing.FromConfig
import com.sgcc.sgd5000.meas.{MeterTimeTag, Meters, ReadingTimeTag}
import com.typesafe.config.ConfigFactory
import rolat.service.scala.backend.{MetricsListener, RouterBackend}
import rolat.service.scala.message.Messages.{RequestMsg, ResponseMsg, Result}
import rolat.service.scala.utils.MessageUtils

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.duration._

/**
  * 定时将任务电表发送给后端
  * 接收后端处理的任务结果保存到数据库
  */
class MasterSingleton(delay: Int,period: Int) extends Actor {
  import context.dispatcher

  val routerBackend = context.actorOf(RouterBackend.props(),"adaptive")
  val tickTask=context.system.scheduler.scheduleOnce(period.seconds , self, "sendJobs")
  val dbActorRouter = context.actorOf(FromConfig.props(Props.empty),name = "dbActorRouter")

  val readingTimeTagMap:mutable.Map[java.lang.Long,ReadingTimeTag]=mutable.Map()
  val meterTimeTagMap:mutable.Map[java.lang.Long,MeterTimeTag]=mutable.Map()
  val meterStatusMap:mutable.Map[java.lang.Long,Int]=mutable.Map()//电表处理状态0未处理，1处理中，2处理结束

  var startTime:Long=_


  override def receive: Receive = {
    case "sendJobs" => println("sendJobs")
      dbActorRouter ! RequestMsg("getReadingTimeTagList",
        MessageUtils.createRequestMsg(1,"rolat.repository.service.IMetersService","metersService","getReadingTimeTagList"))
      dbActorRouter ! RequestMsg("getMeterTimeTagList",
        MessageUtils.createRequestMsg(1,"rolat.repository.service.IMetersService","metersService","getMeterTimeTagList"))
      dbActorRouter ! RequestMsg("getMetersList",
        MessageUtils.createRequestMsg(1,"rolat.repository.service.IMetersService","metersService","getMetersList"))
    case responseMsg:ResponseMsg =>
      responseMsg.id match {
        case "getMetersList"=>{
          val metersList=responseMsg.response.get("result").get.asInstanceOf[util.List[Meters]]
          processMetersList(metersList)
      }
        case "getReadingTimeTagList" =>{
          val readingTimeTagList=responseMsg.response.get("result").get.asInstanceOf[util.List[ReadingTimeTag]]
          processReadingTimeTag(readingTimeTagList)
        }
        case "getMeterTimeTagList" =>{
          val meterTimeTagList=responseMsg.response.get("result").get.asInstanceOf[util.List[MeterTimeTag]]
          processMeterTimeTag(meterTimeTagList)
        }
      }
    case result: Result => println(result.code+"||"+result.context+"耗时"+(result.proceeTime/1000-startTime/1000))
    case _ => sender() ! "未知道"
  }
 /* override def postRestart(reason: Throwable): Unit = ()*///保证preStart只执行一次

  override def preStart(): Unit ={
    //cluster.subscribe(self, classOf[MemberEvent], classOf[ReachabilityEvent])可记录集群可达事件
  }
  override def postStop(): Unit = {
    //cluster.unsubscribe(self)
    tickTask.cancel()
  }
  def processReadingTimeTag(readingTimeTagList:List[ReadingTimeTag]): Unit ={
    for (readingTimeTag <- readingTimeTagList.asScala) {
      readingTimeTagMap.put(readingTimeTag.getMeterId,readingTimeTag)
    }
  }

  def processMeterTimeTag(meterTimeTagList:List[MeterTimeTag]): Unit ={
    for (meterTimeTag <- meterTimeTagList.asScala) {
      meterTimeTagMap.put(meterTimeTag.getMeterId,meterTimeTag)
    }
  }

  def processMetersList(metersList: util.List[Meters]): Unit = {
    for (meters <- metersList.asScala) {
      println(meters.getMeterName)
      val meterId=meters.getMeterId
      val status=meterStatusMap.get(meterId).getOrElse(0)
      if(status!=1){
        val readingTimeTag=readingTimeTagMap.get(meterId)
        val meterTimeTag=meterTimeTagMap.get(meterId)
        if(readingTimeTag!=None&&meterTimeTag!=None){
          val endTime=readingTimeTag.get.getClass2TimeTag
          val startTime=meterTimeTag.get.getClass1TimeTag
          println(startTime+"}}"+endTime)
          if(startTime!=null&&endTime!=null&&endTime.getTime>startTime.getTime+300001){//大于1个周期才做
          val map=Map[String,Any]("requestType"->1,"meters"->meters,
            "meterTimeTag"->meterTimeTag.get,"readingTimeTag"->readingTimeTag.get,"updateTime"->true)
            routerBackend ! RequestMsg("processHisViewAcq",map)
          }

        }
      }
    }
  }

}

object MasterSingleton{

  val prop: Properties = new Properties
  val inputStream = MasterSingleton.getClass.getClassLoader.getResourceAsStream("init.properties")
  prop.load(inputStream)
  val delay =prop.getProperty("master.period")
  val provied =prop.getProperty("master.period")



  def propsRouterFrontend = Props(new MasterSingleton(delay.toInt,provied.toInt))

  def create(port: Int) = {
    val config = ConfigFactory.parseString("akka.cluster.roles = [routerFrontend]")
      .withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port"))
      .withFallback(ConfigFactory.load("adaptive"))
    val calcSystem = ActorSystem("calcClusterSystem",config)
    println("work节点和db节点服务启动后启动Master"+port)
    Cluster(calcSystem) registerOnMemberUp {
      println("Master节点服务启动成功"+port)


      calcSystem.actorOf(ClusterSingletonManager.props(
        MasterSingleton.propsRouterFrontend,
        PoisonPill,
        ClusterSingletonManagerSettings.create(calcSystem).withRole("routerFrontend")
      ), "routerFrontend")

      calcSystem.actorOf(
        ClusterSingletonProxy.props(
          "/user/routerFrontend",
          ClusterSingletonProxySettings.create(calcSystem).withRole("routerFrontend")),
        "routerFrontend-proxy");

      val monitorFlag=prop.getProperty("monitor.service")
      monitorFlag match {
        case "true" => calcSystem.actorOf(Props[MetricsListener], name = "metricsListener")
        case msg@_ => println(msg)
      }

    }
  }
}
