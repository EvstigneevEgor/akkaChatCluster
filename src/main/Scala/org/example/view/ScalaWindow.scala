package org.example.view

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import javafx.application.{Application, Platform}
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.HBox
import javafx.stage.Stage
import org.example.Frontend
import org.example.Frontend.{Die, Event}

import java.io.IOException
import scala.concurrent.duration.DurationInt

class ScalaWindow extends Application {
  private var primaryStage: Stage = null
  implicit val timeout: Timeout = 5.seconds

  val LocalPort = ConfigFactory.load().getInt("MyPort")

  def main(args: Array[String]): Unit = {

    Application.launch()
  }


  override def start(primaryStage: Stage): Unit = {
    try {

      this.primaryStage = primaryStage
      //MainScala cluster = new MainScala();
      primaryStage.setTitle("\u041f\u0440\u0438\u043c\u0435\u0440" + LocalPort)
      showBaseWindow()
    } catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }

  override def stop(): Unit = {
    frontend.foreach(_.tell(Die()))
    Platform.exit()
    System.exit(0)

    super.stop()
  }

  var frontend: Option[ActorRef[Event]] = None

  def showBaseWindow(): Unit = {
    try {
      val loader: FXMLLoader = new FXMLLoader
      loader.setLocation(classOf[Controller].getResource("/main.fxml"))
      val rootLayout: HBox = loader.load
      val scene: Scene = new Scene(rootLayout)
      val scala: ControllerScala = loader.getController[ControllerScala]
      frontend = Some(startup(LocalPort, scala))
      scala.setFrontend(frontend.get)
      primaryStage.setScene(scene)
      primaryStage.show()
    } catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }

  def startup(port: Int, scala: ControllerScala): ActorSystem[Event] = {
    // Override the configuration of the port and role
    val config = ConfigFactory
      .parseString(
        s"""
        akka.remote.artery.canonical.port=$port
        """)
      // ¯\_(ツ)_/¯
      .withFallback(ConfigFactory.load(ConfigFactory.load()))
    val frontend = new Frontend(scala, port.toString)
    ActorSystem[Event](frontend.getActor, "system", config)
  }
}
