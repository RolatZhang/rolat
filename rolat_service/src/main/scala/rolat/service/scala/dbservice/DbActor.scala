package rolat.service.scala.dbservice

import java.sql.Timestamp
import java.util
import java.util.{List, Properties}

import akka.actor.{Actor, ActorSystem, Props}
import akka.routing.BalancingPool
import com.dlptech.swap.tools.SpringUtils
import com.sgcc.sgd5000.constants.EMeterDataItem
import com.sgcc.sgd5000.hisdata.HisViewAcq
import com.sgcc.sgd5000.meas.{MeterTimeTag, Meters, ReadingTimeTag}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import org.springframework.context.support.ClassPathXmlApplicationContext
import rolat.repository.controller.ServiceHolder
import rolat.repository.utils.BeansUtils
import rolat.service.scala.backend.MetricsListener
import rolat.service.scala.message.Messages.{RequestMsg, ResponseMsg}

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * 数据库相关，前后都需要连接
  */
class DbActor(serviceHolder: ServiceHolder) extends Actor{

  val log=LoggerFactory.getLogger(classOf[DbActor])

  val classCacheMap:mutable.Map[String, Any]=mutable.Map()

  override def preStart(): Unit = {
    log.warn("DbActor 重启")
  }

  override def receive: Receive = {
    case requestMsg:RequestMsg =>{
      println("收到消息"+requestMsg)
      requestMsg.id match {
        case "getHisViewAcqList" =>{
          val meters=requestMsg.request.get("meters").get.asInstanceOf[Meters]
          val meterTimeTag=requestMsg.request.get("meterTimeTag").get.asInstanceOf[MeterTimeTag]
          val readingTimeTag=requestMsg.request.get("readingTimeTag").get.asInstanceOf[ReadingTimeTag]
          val updateTime=requestMsg.request.get("updateTime").get
          val meterConfigItem=serviceHolder.metersService.getMeterConfigItem(meters,EMeterDataItem.TOU.getValue)
          val hisViewAcqList=serviceHolder.hisViewAcqService.getHisViewAcqList(meterTimeTag.getClass1TimeTag
            ,readingTimeTag.getClass2TimeTag,meters)
          val map=Map[String,Any]("responseType"->1,"hisViewAcqList"->hisViewAcqList,
            "meterConfigItem"->meterConfigItem,"updateTime"->updateTime)
          sender() ! ResponseMsg(requestMsg.id,map)

        }
        case msg@_=>parse(requestMsg)
      }
    }
    case responseMsg:ResponseMsg =>{
      responseMsg.id match {
        case "saveHisViewAcqList" =>{
          val hisViewAcqList=responseMsg.response.get("hisViewAcqList").get.asInstanceOf[List[HisViewAcq]]
          val updateTime=responseMsg.response.get("updateTime").get
          log.info("是否刷新"+updateTime)
          for (hisViewAcq <- hisViewAcqList.asScala ) {
            log.info(hisViewAcq.getId.getMeterId+"|"+hisViewAcq.getId.getOccurTime+"|"+hisViewAcq.getPapValue)
          }
//          serviceHolder.hisViewAcqService.updateHisViewAcqList(hisViewAcqList,)
        }
      }
    }
    case msg@_=> println("未知消息"+msg)
  }

  def parse(requestMsg:RequestMsg): Unit ={
    val requestMap=requestMsg.request
    val requestType=requestMap.get("requestType") //1查询
    requestType.getOrElse(0) match {
      case 1=>{
        val className=requestMap.get("className").get.toString
        val beanName=requestMap.get("beanName").get.toString
        val methodName=requestMap.get("methodName").get.toString
        if(classCacheMap.get(className)==None){
          val c2 = Class.forName(className)
          val obj = BeansUtils.getBean(beanName, c2)
          classCacheMap +=(beanName ->obj)
        }
        val obj=classCacheMap.get(beanName).get
        val result=obj.getClass.getDeclaredMethod(methodName).invoke(obj)
        val map=Map[String,Any]("result"->result)
        sender() ! ResponseMsg(requestMsg.id,map )


        /* methodName match {
                 case "getMetersList" =>sender() ! ResponseMsg(requestMsg.id, getMetersList)
                 case "getReadingTimeTagList" =>sender() ! ResponseMsg(requestMsg.id, getReadingTimeTag)
                 case "getMeterTimeTagList" =>sender() ! ResponseMsg(requestMsg.id,getMeterTimeTag )
               }*/
   /*   methodName.getOrElse("noMethodName") match {
        case "getMetersList" =>
          val c2 = Class.forName("rolat.repository.service.IMetersService")
          val obj = BeansUtils.getBean("metersService", c2)
          val meterList=obj.getClass.getDeclaredMethod("getMetersList").invoke(obj)

          sender() ! ResponseMsg(requestMsg.id, meterList)
//                 sender() ! ResponseMsg(requestMsg.id,getMetersList)
        case _ => println("未知消息" + _)
      }*/
    }
    }
  }

  def getMetersList(): util.ArrayList[Meters] ={
    val metersList=new util.ArrayList[Meters]
    for (i <- 1 to 100){
        val meters =new Meters()
      meters.setMeterId(i.asInstanceOf[Long])
      meters.setMeterName("电表"+i)
      metersList.add(meters)
    }
    metersList
  }

  def getMeterTimeTag(): util.ArrayList[MeterTimeTag] ={
    val metersList=new util.ArrayList[MeterTimeTag]
    for (i <- 1 to 100){
      val meters =new MeterTimeTag()
      meters.setMeterId(i.asInstanceOf[Long])
      meters.setClass2DummyTimeTag(new Timestamp(System.currentTimeMillis()))
      metersList.add(meters)
    }
    metersList
  }

  def getReadingTimeTag(): util.ArrayList[ReadingTimeTag] ={
    val metersList=new util.ArrayList[ReadingTimeTag]
    for (i <- 1 to 100){
      val meters =new ReadingTimeTag()
      meters.setMeterId(i.asInstanceOf[Long])
      meters.setClass1TimeTag(new Timestamp(System.currentTimeMillis()))
      metersList.add(meters)
    }
    metersList
  }

  def executeBymethodName(): Unit ={

  }

}

object DbActor {

  val prop: Properties = new Properties
  val inputStream = DbActor.getClass.getClassLoader.getResourceAsStream("init.properties")
  prop.load(inputStream)
  val num =prop.getProperty("dber.num")

  def propsDbActor(serviceHolder: ServiceHolder) = Props(new DbActor(serviceHolder))

  def create(port: Int) = {
    val config = ConfigFactory.parseString("akka.cluster.roles = [dbActor]")
      .withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port"))
      .withFallback(ConfigFactory.load())
    val calcSystem = ActorSystem("calcClusterSystem", config)
    println("启动数据库服务" + port)
    val springContext = new ClassPathXmlApplicationContext("./applicationContext.xml")
    SpringUtils.installApplicationContext(springContext)
    val serviceHolder = springContext.getBean("serviceHolder").asInstanceOf[ServiceHolder]

    val dbActors = calcSystem.actorOf(DbActor.propsDbActor(serviceHolder).withDispatcher("db-dispatcher").
      withRouter(new BalancingPool(num.toInt)), "dbActor")

    val monitorFlag=prop.getProperty("monitor.service")
    monitorFlag match {
      case "true" => calcSystem.actorOf(Props[MetricsListener], name = "metricsListener")
      case msg@_ => println(msg)
    }
  }
}
