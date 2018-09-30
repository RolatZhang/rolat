package rolat.service.scala.utils

object MessageUtils {

  def createRequestMsg(requestType:Int,className:String,beanName:String,methodName:String):Map[String,Any]={
    var map=Map[String,Any]("requestType"->requestType,"className"->className,
      "beanName"->beanName,
      "methodName"->methodName)
    map
  }

}
