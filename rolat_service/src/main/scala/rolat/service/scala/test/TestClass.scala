package rolat.service.scala.test

object TestClass {
  def create(taskType:String="routerBackend") = new TestClass(taskType)
}


class TestClass(taskType:String ="routerBackend" ) {
  def getTaskType=taskType
}

