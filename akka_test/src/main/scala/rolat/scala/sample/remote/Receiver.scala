package rolat.scala.sample.remote

import akka.actor.Actor
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.actor.Props

object Receiver {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Sys", ConfigFactory.load("remotelookup"))
    system.actorOf(Props[Receiver], "rcv")
  }
}

class Receiver extends Actor {
  import Sender._

  def receive = {
    case m: Echo  => {
      println("接收到：{} 消息",m)
      sender() ! m
    }
    case Shutdown => context.system.terminate()
    case payload: Array[Byte] => println("接收到内容"+new String(payload).charAt(0))
    case a:Int => println("未知类型"+a)
    case _        => println("未知类型"+_)
  }
}

