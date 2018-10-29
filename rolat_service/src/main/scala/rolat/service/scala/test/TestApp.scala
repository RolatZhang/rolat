package rolat.service.scala.test
import scala.collection.mutable.Map

object TestApp extends App {
  val map=Map[String,Any]("responseType"->1,"hisViewAcqList"->2)
  println(map.get("responseType").get)
   map +=("responseType"->3)
  println(map.get("responseType").get)
}
