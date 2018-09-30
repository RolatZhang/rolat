package rolat.scala.message

import rolat.scala.message.Messages._


object LoadBalancerDemo extends App {

  Calculator.create(2551)   //seed-node
  Calculator.create(25511)      //backend node
  Calculator.create(25512)
  Calculator.create(25513)
  Calculator.create(25514)
  Calculator.create(25515)

  Thread.sleep(2000)

  FrontEnd.create


  Thread.sleep(2000)

  val router = FrontEnd.getRouter

  router ! Add(10,3)
  router ! Mul(3,7)
  router ! Div(8,2)
  router ! Sub(45, 3)
  router ! Div(8,0)

  Thread.sleep(2000)

  router ! Add(10,3)
  router ! Mul(3,7)
  router ! Div(8,2)
  router ! Sub(45, 3)
  router ! Div(8,0)

}