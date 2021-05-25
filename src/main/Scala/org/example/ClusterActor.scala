package org.example

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, UnreachableMember}

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

  override def receive: Receive = ???
}
