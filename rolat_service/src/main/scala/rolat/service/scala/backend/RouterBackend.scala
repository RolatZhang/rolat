package rolat.service.scala.backend

import akka.actor._
import akka.routing.FromConfig
import com.sgcc.sgd5000.meas.Meters

/**
  * 启动种子节点
  * 将任务分发到后台对应actor
  */
object RouterBackend {
  def props(taskType:String="routerBackend") = Props(new RouterBackend(taskType))
}

/**
  *
  * 转发消息到相应的actor
  */
class RouterBackend(taskType:String ="routerBackend") extends Actor {

  def getTaskType=taskType
  val routerBackend = context.actorOf(FromConfig.props(Props.empty),
    name = taskType)

  override def receive: Receive = {
    case meters:Meters => println(meters.getMeterName)
      routerBackend forward meters
    case msg@ _ => {
      println(msg+"未匹配")
      routerBackend forward msg
    }
  }
}