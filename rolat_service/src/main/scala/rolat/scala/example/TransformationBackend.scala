package rolat.scala.example

import akka.actor.{Actor, ActorRef, RootActorPath, Terminated}
import akka.cluster.{Cluster, Member, MemberStatus}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}

final case class TransformationJob(text: String)
final case class TransformationResult(text: String)
final case class JobFailed(reason: String, job: TransformationJob)
case object BackendRegistration

class TransformationBackend extends Actor {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, MemberUp
  // re-subscribe when restart
  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])
  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case TransformationJob(text) ⇒ sender() ! TransformationResult(text.toUpperCase)
    case state: CurrentClusterState ⇒
      state.members.filter(_.status == MemberStatus.Up) foreach register
    case MemberUp(m) ⇒ register(m)
  }

  def register(member: Member): Unit =
    if (member.hasRole("frontend"))
      context.actorSelection(RootActorPath(member.address) / "user" / "frontend") !
        BackendRegistration
}

class TransformationFrontend extends Actor {

  var backends = IndexedSeq.empty[ActorRef]
  var jobCounter = 0

  def receive = {
    case job: TransformationJob if backends.isEmpty ⇒
      sender() ! JobFailed("Service unavailable, try again later", job)

    case job: TransformationJob ⇒
      jobCounter += 1
      backends(jobCounter % backends.size) forward job

    case BackendRegistration if !backends.contains(sender()) ⇒
      context watch sender()
      backends = backends :+ sender()

    case Terminated(a) ⇒
      backends = backends.filterNot(_ == a)
  }
}