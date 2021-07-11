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
  var Name:String = null
  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    if (counter == 0)
    //startThread()
    {
      timer.scheduleAtFixedRate(timerTask, 0, 10 * 1000)
    }


    ContactList.getSelectionModel.setSelectionMode(SelectionMode.SINGLE)
    import javafx.beans.value.ChangeListener
    import javafx.beans.value.ObservableValue
    ContactList.getSelectionModel.selectedItemProperty.addListener(new ChangeListener[Contacts]() {
      override def changed(observable: ObservableValue[_ <: Contacts], oldValue: Contacts, newValue: Contacts): Unit = {
        partner = newValue.Name
        PostText.setPromptText( partner + " : " + Name)
      }
    })
    MessageList.setCellFactory((param: ListView[Message]) => new ListCell[Message]() {
      override protected def updateItem(item: Message, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (empty || item == null || item.getPostText == null) setText(null)
        else setText(item.getPostText)
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
      val m = new Message(PostText.getText,Nck.getText,partner,true)
      //val `def` = new Message
      if (partner!=null && Name!=null){
      MainScala.sendMessage(m)
      if (MessageList.getItems.get(0).getPostText == "\u0412\u0432\u0435\u0434\u0438\u0442\u0435\u0020\u0442\u0435\u043a\u0441\u0442\u0020\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f") MessageList.getItems.remove(0)
      MessageList.getItems.add(m)
      PostText.clear()}
    }
  }

  def Login(): Unit = {
    if (!(Nck.getText == "")) {
      MainScala.reName(Nck.getText)
      Name=Nck.getText
      timerTask.run()
      Nck.setEditable(false)
    }
  }

  def startThread(): Unit = {
    new Thread() {
      override def run(): Unit = { //Do some stuff in another thread

        //while (true) {
        var newlist = MainScala.updateListUser
        if (!newlist.getItems.isEmpty) {
          newlist.getItems.sorted()
          ContactList.getItems.clear()
          ContactList.getItems.addAll(newlist.getItems)

          ContactList.setCellFactory((param: ListView[Contacts]) => new ListCell[Contacts]() {
            override protected def updateItem(item: Contacts, empty: Boolean): Unit = {
              super.updateItem(item, empty)
              if (empty || item == null || item.getName == null) setText(null)
              else setText(item.getName)
            }
          })
        } else {
          println("not update".toUpperCase)
        }


        counter += 1
        Thread.sleep(10)

        // }
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