package rolat.scala.message

object ClusterSingletonDemo extends App {

  SingletonActor.create(2551)    //seed-node

  SingletonActor.create(0)   //ClusterSingletonManager node
  SingletonActor.create(0)
  SingletonActor.create(0)
  SingletonActor.create(0)

  SingletonUser.create(25112)     //ClusterSingletonProxy node

}