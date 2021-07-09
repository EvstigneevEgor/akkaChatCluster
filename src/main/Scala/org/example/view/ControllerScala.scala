/*
package org.example.view

import com.sun.tools.javac.comp.Enter
import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.Initializable
import javafx.scene.control.{ListCell, ListView, SelectionMode}
import javafx.scene.input.{KeyCode, KeyEvent}
import org.example.MainScala
import akka.pattern.ask

import java.net.URL
import java.util.ResourceBundle
import scala.concurrent.duration.DurationInt
import akka.util.Timeout
import org.example.MainScala.system

import scala.collection.mutable
import scala.concurrent.{Await, Promise}
//import akka.util.duration._
class ControllerScala extends Controller with Initializable {


  implicit val timeout = Timeout(3.seconds)
  override def initialize(location: URL, resources: ResourceBundle): Unit = {

    MessageList.setCellFactory((param: ListView[Message]) => new ListCell[Message]() {
      override protected def updateItem(item: Message, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (empty || item == null || item.getPostText == null) setText(null)
        else setText(item.getPostText)
      }
    })
    ContactList.setCellFactory((param: ListView[Contacts]) => new ListCell[Contacts]() {
      override protected def updateItem(item: Contacts, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (empty || item == null || item.getName == null) setText(null)
        else setText(item.getName)
      }
    })

    val n = new Message
    MessageList.getItems.add(n)
    MessageList.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)


    Send.setOnAction((event: ActionEvent) => Sender())
    Logn.setOnAction((event: ActionEvent) => Login())
    PostText.setPromptText("\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435!")
  }

  def setAkka(clusterN: String): Unit = {
  }

  private def Sender(): Unit = {
    if (!(PostText.getText == "")) {
      val m = new Message(PostText.getText)
      val `def` = new Message
      if (MessageList.getItems.get(0).getPostText == "\u0412\u0432\u0435\u0434\u0438\u0442\u0435\u0020\u0442\u0435\u043a\u0441\u0442\u0020\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f") MessageList.getItems.remove(0)
      MessageList.getItems.add(m)
      PostText.clear()
    }
  }

  def Login(): Unit = {
    if (!(Nck.getText == "")){
      //MainScala.SetNick(Nck.getText)
      system.actorSelection(MainScala.Address) ! SetName(Nck.getText)
      ContactList.getItems.add(new Contacts(MainScala.Address,Nck.getText))
      Nck.setEditable(false)
    }
/*
    var s = MainScala.getClusterN.ask(GetAdressName())
    var f =Await.result(s, 10.second).asInstanceOf[mutable.HashMap[String, String]]
    MessageList.getItems.add(new Message(f.mkString(" ")))
*/
  }

  def setCluster(): Unit = {
  }


}
*/
