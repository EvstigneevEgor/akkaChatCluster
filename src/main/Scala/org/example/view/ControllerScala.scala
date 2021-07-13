package org.example.view

import com.sun.tools.javac.comp.Enter
import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.Initializable
import javafx.scene.control.{ListCell, ListView, SelectionMode}
import javafx.scene.input.{KeyCode, KeyEvent}
import org.example.MainScala
import akka.pattern.ask

import java.net.URL
import java.util.{Comparator, ResourceBundle, Timer, TimerTask}
import scala.concurrent.duration.DurationInt
import akka.util.Timeout
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.text.TextAlignment

import scala.collection.mutable
import scala.concurrent.{Await, Promise}

//import akka.util.duration._

class ControllerScala extends Controller with Initializable {

  val timerTask = new MyTimerTask()
  // стартуем TimerTask в виде демона
  timerTask.upd(this)
  val timer = new Timer(true)

  implicit val timeout = Timeout(3.seconds)
  var counter = 0
  var partner: String = null
  var Name: String = null

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    if (counter == 0) {
      timer.scheduleAtFixedRate(timerTask, 1, 10 * 100)
    }

    ContactList.getSelectionModel.setSelectionMode(SelectionMode.SINGLE)
    import javafx.beans.value.ChangeListener
    import javafx.beans.value.ObservableValue
    ContactList.getSelectionModel.selectedItemProperty.addListener(new ChangeListener[Contacts]() {
      override def changed(observable: ObservableValue[_ <: Contacts], oldValue: Contacts, newValue: Contacts): Unit = {
        if (newValue != null)
          partner = newValue.Name
        PostText.setPromptText(partner + " : " + Name)
      }
    })

    ContactList.setCellFactory((param: ListView[Contacts]) => new ListCell[Contacts]() {
      override protected def updateItem(item: Contacts, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (empty || item == null || item.getName == null) setText(null)
        else setText(item.getName)
      }
    })

    MessageList.setCellFactory((param: ListView[Message]) => new ListCell[Message]() {
      override protected def updateItem(item: Message, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (empty || item == null || item.getPostText == null) setText(null)
        else {
          if (item.getFrom != null)
            setText(item.getFrom + ": " + item.getPostText)
          else
            setText(item.getPostText)
          setTextAlignment(if (item.getFrom == Name) TextAlignment.LEFT else TextAlignment.RIGHT)
          setAlignment(
            if (item.getFrom == null)
              Pos.CENTER
            else if (item.getFrom == Name)
              Pos.CENTER_RIGHT
            else
              Pos.CENTER_LEFT

          )
        }
      }
    })

    val n = new Message
    MessageList.getItems.add(n)

    MessageList.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)

    Send.setOnAction((event: ActionEvent) => Sender())
    Logn.setOnAction((event: ActionEvent) => Login())
    PostText.setPromptText("\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435!")
  }


  private def Sender(): Unit = {
    if (!(PostText.getText == "")) { 
      val m = new Message(PostText.getText, Nck.getText, partner)
      //val `def` = new Message
      if (partner != null && Name != null) {
        MainScala.sendMessage(m)
        if (MessageList.getItems.get(0).getPostText == "\u0412\u0432\u0435\u0434\u0438\u0442\u0435\u0020\u0442\u0435\u043a\u0441\u0442\u0020\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f") MessageList.getItems.remove(0)
        MessageList.getItems.add(m)
        PostText.clear()
      }
    }
  }


  def Login(): Unit = {
    if (!(Nck.getText == "")) {
      MainScala.reName(Nck.getText)
      Name = Nck.getText
      timerTask.run()
      Nck.setEditable(false)
    }
  }

  def startThread(): Unit = {
    new Thread() {
      override def run(): Unit = { //Do some stuff in another thread
        if (partner != null) {

          val newMessage = MainScala.updateListMessage(partner)
          if (!newMessage.getItems.isEmpty)
            if (!MessageList.getItems.containsAll(newMessage.getItems)) {
              Platform.runLater(new Runnable() {
                override def run(): Unit = {
                  MessageList.getItems.clear()
                  MessageList.getItems.addAll(newMessage.getItems)
                }
              })
            }
        }

        var newlist = MainScala.updateListUser()
        if (!newlist.getItems.isEmpty) {
          newlist.getItems.sorted()
          Platform.runLater(new Runnable() {
            override def run(): Unit = {
              ContactList.getItems.clear()
              ContactList.getItems.addAll(newlist.getItems)
              ContactList.getItems.add(new Contacts("", "\u041e\u0431\u0449\u0438\u0439\u0020\u0447\u0430\u0442", false))
            }
          })
        }

        counter += 1
        Thread.sleep(10)
      }
    }.start()
  }


}

class MyTimerTask extends TimerTask {
  var Ctr: ControllerScala = null

  def upd(Ctr: ControllerScala): Unit = {
    this.Ctr = Ctr
  }

  override def run(): Unit = {
    Ctr.startThread()
  }
}