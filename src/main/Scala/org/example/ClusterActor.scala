package org.example

import akka.cluster.ClusterEvent._
import akka.actor._
import akka.event._
import akka.cluster._
import akka.cluster.ddata.Replicator.ReadLocal.timeout
import akka.remote.WireFormats.TimeUnit

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationInt}


class ClusterActor extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart() {
    cluster.subscribe(self, InitialStateAsEvents, classOf[MemberEvent],
      classOf[UnreachableMember])
  }

  override def postStop() {
    cluster.unsubscribe(self)
  }

  def receive = LoggingReceive {
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
/*
object AkkaClusterExample extends App {
  val system = ActorSystem("system")

  //val clusterListener = system.actorOf(Props[ClusterActor], "clusterListener")

  system.actorOf(Props[ClusterActor], "clusterActor")
  Await.ready(system.whenTerminated, 365.days)
}
*/