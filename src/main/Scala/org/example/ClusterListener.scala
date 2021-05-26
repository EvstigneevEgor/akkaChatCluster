package org.example

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberRemoved, MemberUp, UnreachableMember}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt


class ClusterListener extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart() {
    cluster.subscribe(self, InitialStateAsEvents, classOf[MemberEvent],
      classOf[UnreachableMember])
  }

  override def postStop() {
    cluster.unsubscribe(self)
  }

  def receive = {
    case MemberUp(member) =>
      log.info(s"[Listener] node is up: $member")


    case UnreachableMember(member) =>
      log.info(s"[Listener] node is unreachable: $member")

    case MemberRemoved(member, prevStatus) =>
      log.info(s"[Listener] node is removed: $member")

    case ev: MemberEvent =>
      log.info(s"[Listener] event: $ev")
  }
}
object AkkaClusterExample extends App {
  val system = ActorSystem("system")

  //val clusterListener = system.actorOf(Props[ClusterActor], "clusterListener")

  system.actorOf(Props[ClusterListener], "clusterListener")
  Await.ready(system.whenTerminated, 365.days)
}