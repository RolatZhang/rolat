package rolat.service.scala.message

import java.sql.Timestamp
import akka.routing.ConsistentHashingRouter.ConsistentHashable
import com.sgcc.sgd5000.meas.Meters

object Messages {
  class MathOps(hashKey: String) extends Serializable with ConsistentHashable {
    override def consistentHashKey: Any = hashKey
  }
  case class HisViewAcq(x: Int, y: Int) extends MathOps("hisViewAcqer")

  sealed trait ClusterMsg
  case class RegisterBackendActor(role: String) extends ClusterMsg

  case class Result(code:Int,context: String,proceeTime: Long)//存放反馈结果

  case class RequestMsg(id:String, request:Map[String,Any]) extends MathOps("RequestMsg")
  case class ResponseMsg(id:String, response:Map[String,Any]) extends MathOps("ResponseMsg")

  case class HisViewAcqTask(meter:Meters,startTime: Timestamp,endTime: Timestamp) extends MathOps("HisViewAcqTask")
}