package org.example

import akka.cluster.ClusterEvent._
import akka.actor._
import akka.event._
import akka.cluster._
import akka.cluster.ddata.Replicator.ReadLocal.timeout
import akka.remote.WireFormats.TimeUnit
import org.example.view.{GetAdress, GetAdressName, MyName, Rename}

import scala.collection.mutable
import scala.collection.mutable.HashMap
import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationInt}
//import akka.actor.{ActorSystem, Props}
import org.example.MainScala.system

class ClusterActor extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  val addressNick = new mutable.HashMap[String, String]////
  val sessions = new HashMap[String, ActorRef]

  var Count = 1;

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
      log.info(s"[Listener] node is up: ${member.address}")
      var sessionBuf = system.actorOf(Props(new Messenger(member.address.toString)))
      sessions+= (sessionBuf.path.toString -> sessionBuf)
      sessions(sessionBuf.path) ! MyName(member.address.toString,Count.toString)
      addressNick+=(sessionBuf.path.toString -> Count.toString)
      Count+=1

    case UnreachableMember(member) =>
      log.info(s"[Listener] node is unreachable: $member")

    case MemberRemoved(member, prevStatus) =>
      log.info(s"[Listener] node is removed: $member")
    case GetAdressName() =>
      sender ! addressNick
    case GetAdress() =>
      sender ! sender()
    case Rename(oldName,newName) =>
      addressNick.update(sender.path.toString,newName)

    case ev: MemberEvent =>
      log.info(s"[Listener] event: $ev")

  }
}
