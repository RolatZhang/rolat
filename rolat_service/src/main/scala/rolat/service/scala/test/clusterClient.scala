/*网址：//https://doc.akka.io/docs/akka/2.4.0/scala/cluster-client.html
package rolat.service.scala.test

import akka.cluster.client.{ClusterClient, ClusterClientSettings}

object clusterClient extends App {
  runOn(client) {
    val c = system.actorOf(ClusterClient.props(
      ClusterClientSettings(system).withInitialContacts(initialContacts)), "client")
    c ! ClusterClient.Send("/user/serviceA", "hello", localAffinity = true)
    c ! ClusterClient.SendToAll("/user/serviceB", "hi")
  }


  val initialContacts = Set(
    ActorPath.fromString("akka.tcp://OtherSys@host1:2552/system/receptionist"),
    ActorPath.fromString("akka.tcp://OtherSys@host2:2552/system/receptionist"))
  val settings = ClusterClientSettings(system)
    .withInitialContacts(initialContacts)
}
*/
