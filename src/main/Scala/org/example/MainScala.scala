package org.example

import akka.actor.{ActorSystem, Props}

class MainScala extends App{
  val system = ActorSystem("system")

  val clusterListener = system.actorOf(Props[ClusterActor], "ClusterActor")

  val sessionManager = system.actorOf(Props[Messenger], "Messenger")

}
