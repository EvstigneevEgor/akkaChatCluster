
package org.example


import akka.actor.{ActorSystem, Props}
import javafx.application.Application
import javafx.application.{Application, Platform}
import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.pattern.ask
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.HBox
import javafx.stage.Stage
import org.example.view.ScalaWindow

import java.io.IOException
import scala.collection.mutable.HashMap
import scala.jdk.CollectionConverters._

object MainScala extends App{
  //def SetNick(getText: String) = NickName=getText
  val Address="akka://system@127.0.0.1:2511"
  val system = ActorSystem("system")
  private var NickName : String = "";
  val clusterListener = system.actorOf(Props[ClusterActor], "ClusterActor")
  //val sessionManager = system.actorOf(Props[Messenger], "Messenger")

  def getClusterN = clusterListener ;

  Application.launch(classOf[ScalaWindow])


}
