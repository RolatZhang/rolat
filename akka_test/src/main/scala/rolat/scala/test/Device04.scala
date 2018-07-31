package rolat.scala.test

import akka.actor.{Actor, ActorLogging, Props}

object Device04 {
  def props(groupId: String, deviceId: String): Props = Props(new Device04(groupId, deviceId))

  final case class ReadTemperature(requestId: Long)
  final case class RespondTemperature(requestId: Long, value: Option[Double])
}

class Device04(groupId: String, deviceId: String) extends Actor with ActorLogging {
  import Device04._

  var lastTemperatureReading: Option[Double] = None

  override def preStart(): Unit = log.info("Device actor {}-{} started", groupId, deviceId)
  override def postStop(): Unit = log.info("Device actor {}-{} stopped", groupId, deviceId)

  override def receive: Receive = {
    case ReadTemperature(id) ⇒
      sender() ! RespondTemperature(id, lastTemperatureReading)
  }

}