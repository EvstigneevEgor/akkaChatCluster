
package org.example


import akka.actor.{ActorSystem, Props}
import javafx.application.Application
import FxStart._
import javafx.application.{Application, Platform}
import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.HBox
import javafx.stage.Stage

import java.io.IOException
import scala.collection.mutable.HashMap
import scala.jdk.CollectionConverters._

object MainScala extends App{
  val system = ActorSystem("system")

  val clusterListener = system.actorOf(Props[ClusterActor], "ClusterActor")

  def getClusterN = clusterListener;
  //val sessionManager = system.actorOf(Props[Messenger], "Messenger")
  Application.launch(classOf[ScalaWindow])
}
