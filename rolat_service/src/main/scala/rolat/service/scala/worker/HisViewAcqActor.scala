package rolat.service.scala.worker

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.sgcc.sgd5000.hisdata.HisViewAcq

import scala.collection.JavaConverters._
import java.util.List

import com.sgcc.sgd5000.meas.MeterConfigItem
import rolat.service.scala.message.Messages.{RequestMsg, ResponseMsg}

/**
  *  收到任务，做完任务，存储到db中，将节点发到下一个节点
  */
class HisViewAcqActor() extends Actor with ActorLogging {

  override def receive: Receive = {
    case requestMsg:RequestMsg =>{
      requestMsg.id match {
        case "processHisViewAcqList"=>{
          val requestMsgMap=requestMsg.request
          val hisViewAcqList=requestMsgMap.get("hisViewAcqList").get.asInstanceOf[List[HisViewAcq]]
          val meterConfigItem=requestMsgMap.get("meterConfigItem").get.asInstanceOf[MeterConfigItem]
          val updateTime=requestMsgMap.get("updateTime").get
          val result=processHisViewAcqList(hisViewAcqList,meterConfigItem)

          val map=Map[String,Any]("responseType"->1,"hisViewAcqList"->result,"updateTime"->updateTime)
          sender() ! ResponseMsg(requestMsg.id,map)
        }
      }
    }
    case msg@_ => println(msg)
  }

  def processHisViewAcqList(hisViewAcqList:List[HisViewAcq],meterConfigItem:MeterConfigItem): List[HisViewAcq] ={
    for (hisViewAcq <- hisViewAcqList.asScala ){
      if(hisViewAcq.getPapRawValue!=null)
      setHisViewAcq(hisViewAcq,meterConfigItem)
    }
    hisViewAcqList
  }


  def setHisViewAcq(hisViewAcq:HisViewAcq,meterConfigItem:MeterConfigItem): Unit ={
    val precision=meterConfigItem.getPrecision
    if(precision!=null){
      hisViewAcq.setPapStatus(hisViewAcq.getPapRawStatus)
      hisViewAcq.setPapStatus(hisViewAcq.getPapRawStatus)
      hisViewAcq.setPapStatus(hisViewAcq.getPapRawStatus)
      hisViewAcq.setPapStatus(hisViewAcq.getPapRawStatus)
      if(hisViewAcq.getPapRawValue!=null) hisViewAcq.setPapValue(hisViewAcq.getPapRawValue*precision)
      if(hisViewAcq.getRapRawValue!=null) hisViewAcq.setRapValue(hisViewAcq.getRapRawValue*precision)
      if(hisViewAcq.getPrpRawValue!=null) hisViewAcq.setPrpValue(hisViewAcq.getPrpRawValue*precision)
      if(hisViewAcq.getRrpRawValue!=null) hisViewAcq.setRrpValue(hisViewAcq.getRrpRawValue*precision)
    }
  }
}
