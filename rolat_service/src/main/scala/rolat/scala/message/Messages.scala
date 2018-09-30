package rolat.scala.message

import akka.routing.ConsistentHashingRouter.ConsistentHashable

object Messages {
  class MathOps(hashKey: String) extends Serializable with ConsistentHashable {
    override def consistentHashKey: Any = hashKey
  }
  case class Add(x: Int, y: Int) extends MathOps("adder")
  case class Sub(x: Int, y: Int) extends MathOps("substractor")
  case class Mul(x: Int, y: Int) extends MathOps("multiplier")
  case class Div(x: Int, y: Int) extends MathOps("divider")



  case class HisViewAcq(x: Int, y: Int) extends MathOps("hisViewAcqer")

  sealed trait ClusterMsg
  case class RegisterBackendActor(role: String) extends ClusterMsg
}