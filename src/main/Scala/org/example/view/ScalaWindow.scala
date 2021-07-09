/*
package org.example.view

import akka.actor.ActorRef
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.HBox
import javafx.stage.Stage
import org.example.MainScala

import java.io.IOException

class ScalaWindow extends Application{
  private var primaryStage: Stage = null

  private var aktor: ActorRef = MainScala.getClusterN
  def main(args: Array[String]): Unit = {

    Application.launch()
  }


  override def start(primaryStage: Stage): Unit = {
    try {
      this.primaryStage = primaryStage
      //MainScala cluster = new MainScala();
      primaryStage.setTitle("\u041f\u0440\u0438\u043c\u0435\u0440"+aktor.path.toString)
      //primaryStage.setWidth(900);
      //primaryStage.setHeight(500);
      showBaseWindow()
    } catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }

  def showBaseWindow(): Unit = {
    try {
      val loader: FXMLLoader = new FXMLLoader
      loader.setLocation(classOf[Controller].getResource("/main.fxml"))
      val rootLayout: HBox = loader.load
      val scene: Scene = new Scene(rootLayout)

      primaryStage.setScene(scene)//loader.getController
      primaryStage.show()
    } catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }
}
*/