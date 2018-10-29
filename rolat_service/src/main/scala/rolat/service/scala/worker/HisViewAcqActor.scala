package rolat.service.scala.worker

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.sgcc.sgd5000.hisdata.HisViewAcq

import scala.collection.JavaConverters._
import java.util.List

import com.sgcc.sgd5000.meas.{MeterConfigItem, MeterTimeTag, Meters, ReadingTimeTag}
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
          val meterTimeTag=requestMsgMap.get("meterTimeTag").get.asInstanceOf[MeterTimeTag]
          val readingTimeTag=requestMsgMap.get("readingTimeTag").get.asInstanceOf[ReadingTimeTag]
          val meters=requestMsgMap.get("meters").get.asInstanceOf[Meters]
          val result=processHisViewAcqList(hisViewAcqList,meterConfigItem)

          val meterConfigItemProfile=requestMsgMap.get("meterConfigItemProfile").get.asInstanceOf[MeterConfigItem]
          val hisProfileList=processHisProfileList(result,meterConfigItemProfile)

          val map=Map[String,Any]("responseType"->1,"hisViewAcqList"->result,"meterConfigItem"->meterConfigItem,
            "updateTime"->updateTime,"meterTimeTag"->meterTimeTag,"readingTimeTag"->readingTimeTag,"meters"->meters)
          sender() ! ResponseMsg(requestMsg.id,map)
        }
      }
    }
    case msg@_ => println(msg)
  }

  def processHisProfileList(hisViewAcqList:List[HisViewAcq],meterConfigItem:MeterConfigItem): List[HisViewAcq] ={
    val totalSize = hisViewAcqList.size
    var lastView:HisViewAcq= null
    var i=0
    import java.util
    val resultList = new util.ArrayList[HisViewAcq] //有效底码的
    for (hisViewAcq <- hisViewAcqList.asScala ){
      var nextview:HisViewAcq = null
      if ( i < totalSize - 1) {
        nextview = hisViewAcqList.get(i + 1)
      }
      val judge = judgeViewException(lastView, hisViewAcq, nextview)
      if (judge) {
        lastView = hisViewAcq
        resultList.add(hisViewAcq)
      }
      i+=1
    }


    resultList
  }

  def processHisViewAcqList(hisViewAcqList:List[HisViewAcq],meterConfigItem:MeterConfigItem): List[HisViewAcq] ={
    val totalSize = hisViewAcqList.size
    var lastView:HisViewAcq= null
    var i=0
    import java.util
    val resultList = new util.ArrayList[HisViewAcq] //有效底码的
    for (hisViewAcq <- hisViewAcqList.asScala ){
      if(hisViewAcq.getPapRawValue!=null) setHisViewAcq(hisViewAcq,meterConfigItem)
      //对底码进行校验//对底码进行校验
      var nextview:HisViewAcq = null
      if ( i < totalSize - 1) {
        nextview = hisViewAcqList.get(i + 1)
      }
      val judge = judgeViewException(lastView, hisViewAcq, nextview)
      if (judge) {
        lastView = hisViewAcq
        resultList.add(hisViewAcq)
      }
      i+=1
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

  import net.njcp.service.util.CommFunc
  import net.njcp.service.util.Constants

  private def judgeViewException(lastview: HisViewAcq, view: HisViewAcq, nextview: HisViewAcq) = { //lastview是库里多存入的一个点
    //Double adjustNum=0.000001;精度调节系数
    var judge = true
    if (lastview != null) {
      val lastpapVaule = lastview.getPapRawValue
      val lastrapVaule = lastview.getRapRawValue
      val lastprpVaule = lastview.getPrpRawValue
      val lastrrpVaule = lastview.getRrpRawValue
      if (lastpapVaule == null || lastrapVaule == null || lastprpVaule == null || lastrrpVaule == null) { //上一个点如果异常 取下一个点
        judge = true
      }
      else {
        val papVaule = view.getPapRawValue
        val rapVaule = view.getRapRawValue
        val prpVaule = view.getPrpRawValue
        val rrpVaule = view.getRrpRawValue
        if (papVaule == null || rapVaule == null || prpVaule == null || rrpVaule == null) judge = false
        else { //判断倒走
          if (judge && papVaule < lastpapVaule) { //倒走
            judge = false
            if (nextview != null && nextview.getPapValue != null && nextview.getPapValue > papVaule && lastpapVaule < nextview.getPapValue) { //疑似换表或满码
              judge = true
            }
            else {
              val newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_REVERSE, 1, view.getPapStatus)
              view.setPapStatus(newStatus)
            }
          }
          if (judge && rapVaule < lastrapVaule) {
            judge = false
            if (nextview != null && nextview.getRapValue != null && nextview.getRapValue > rapVaule && lastrapVaule < nextview.getRapValue) judge = true
            else {
              val newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_REVERSE, 1, view.getRapStatus)
              view.setRapStatus(newStatus)
            }
          }
          if (judge && prpVaule < lastprpVaule) {
            judge = false
            if (nextview != null && nextview.getPrpValue != null && nextview.getPrpValue > prpVaule && lastprpVaule < nextview.getPrpValue) judge = true
            else {
              val newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_REVERSE, 1, view.getPrpStatus)
              view.setPrpStatus(newStatus)
            }
          }
          if (judge && rrpVaule < lastrrpVaule) {
            judge = false
            if (nextview != null && nextview.getRrpValue != null && nextview.getRrpValue > rrpVaule && lastrrpVaule < nextview.getRrpValue) judge = true
            else {
              val newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_REVERSE, 1, view.getRrpStatus)
              view.setRrpStatus(newStatus)
            }
          }
          //判断冒大数
          if (lastpapVaule > 0 && papVaule > 1000 && papVaule / lastpapVaule > 10) { //冒大数
            judge = false
            val newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_TREMENDOUS, 1, view.getPapStatus)
            view.setPapStatus(newStatus)
          }
          if (lastrapVaule > 0 && rapVaule > 1000 && rapVaule / lastrapVaule > 10) {
            judge = false
            val newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_TREMENDOUS, 1, view.getRapStatus)
            view.setRapStatus(newStatus)
          }
          if (lastprpVaule > 0 && prpVaule > 1000 && prpVaule / lastprpVaule > 10) {
            judge = false
            val newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_TREMENDOUS, 1, view.getPrpStatus)
            view.setPrpStatus(newStatus)
          }
          if (lastrrpVaule > 0 && rrpVaule > 1000 && rrpVaule / lastrrpVaule > 10) {
            judge = false
            val newStatus = CommFunc.getDataStatusBy(Constants.METER_STATUS_TREMENDOUS, 1, view.getRrpStatus)
            view.setRrpStatus(newStatus)
          }
        }
      }
    }
    judge
  }
}
