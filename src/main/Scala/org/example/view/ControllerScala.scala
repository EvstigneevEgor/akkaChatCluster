package org.example.view

import akka.actor.typed.ActorRef
import akka.util.Timeout
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.control.{ListCell, ListView, SelectionMode}
import javafx.scene.text.TextAlignment
import org.example.Frontend._

import java.net.URL
import java.util.{ResourceBundle, Timer, TimerTask}
import scala.concurrent.duration.DurationInt

class ControllerScala extends Controller with Initializable {

  var frontend: Option[ActorRef[Event]] = None

  def setFrontend(fr: ActorRef[Event]) = frontend = Some(fr)

  val timerTask = new MyTimerTask()
  timerTask.upd(this)
  val timer = new Timer(true)

  implicit val timeout = Timeout(3.seconds)
  var counter = 0
  var partner: SimpleStringProperty = new SimpleStringProperty()
  var Name: String = null

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    if (counter == 0) {
      timer.scheduleAtFixedRate(timerTask, 1, 10 * 100)
    }

    ContactList.getSelectionModel.setSelectionMode(SelectionMode.SINGLE)
    ContactList.getSelectionModel.selectedItemProperty.addListener(new ChangeListener[Contacts]() {
      override def changed(observable: ObservableValue[_ <: Contacts], oldValue: Contacts, newValue: Contacts): Unit = {
        if (newValue != null)
          partner.setValue(newValue.Name)
        PostText.setPromptText(partner + " : " + Name)
      }
    })

    ContactList.setCellFactory(_ => new ListCell[Contacts]() {
      override protected def updateItem(item: Contacts, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (empty || item == null || item.getName == null) setText(null)
        else {
          setText(item.getName)
          updateListMessage(item.getName) //todo here is bug. Fix that. Address doesn`t work. or somewhere threadRacing
        }
      }
    })

    MessageList.setCellFactory(_ => new ListCell[Message]() {
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

    Send.setOnAction(_ => Sender())
    Logn.setOnAction(_ => Login())
    partner.addListener(new ChangeListener[String]() {
      override def changed(observable: ObservableValue[_ <: String], oldValue: String, newPartnerName: String): Unit = {
        if (newPartnerName != null)
          updateListMessage(newPartnerName)
        updateListUser()
      }
    })
    updateListUser()
    PostText.setPromptText("\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435!")
  }


  private def Sender(): Unit = {
    if (!(PostText.getText == "")) {
      val m = new Message(PostText.getText, Nck.getText, partner.getValue)
      if (partner != null && Name != null) {
        sendMessage(m)
        if (MessageList.getItems.get(0).getPostText == "\u0412\u0432\u0435\u0434\u0438\u0442\u0435\u0020\u0442\u0435\u043a\u0441\u0442\u0020\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f") MessageList.getItems.remove(0)
        MessageList.getItems.add(m)
        PostText.clear()
      }
    }
  }


  def Login(): Unit = {
    if (!(Nck.getText == "")) {
      reName(Nck.getText)
      Name = Nck.getText
      timerTask.run()
      Nck.setEditable(false)
    }
  }

  def startThread(): Unit = {
    new Thread() {
      override def run(): Unit = { //Do some stuff in another thread
        //        if (partner.getValue != null)
        //          updateListMessage(partner.getValue)
        //        updateListUser
        counter += 1
        Thread.sleep(10)
      }
    }.start()
  }

  def updateContactsList(newlist: ListView[Contacts]): Unit = {
    if (!newlist.getItems.isEmpty) {
      newlist.getItems.sorted()
      ContactList.getItems.clear()
      ContactList.getItems.addAll(newlist.getItems)
      ContactList.getItems.add(new Contacts("", "\u041e\u0431\u0449\u0438\u0439\u0020\u0447\u0430\u0442", false))
    }
  }

  def updateListUser() = {
    frontend.foreach(fr => fr.tell(UpdateInformations))
  }

  def reName(newName: String): Unit = {
    frontend.foreach(_.tell(NewNameF(newName)))

  }

  def sendMessage(message: Message): Unit = {
    frontend.foreach(_.tell(SendMessageF(message)))
  }

  def updateListMessage(name: String): Unit = {
    frontend
      .foreach(fr => fr.tell(UpdateMessage(name)))
  }

  def updateMessages(messages: List[Message]): Unit = {
    val listMessage = new ListView[Message]
    for (i <- messages) {
      listMessage.getItems.add(i)
    }
    if (!listMessage.getItems.isEmpty)
      if (!MessageList.getItems.containsAll(listMessage.getItems)) {
        Platform.runLater(new Runnable() {
          override def run(): Unit = {
            MessageList.getItems.clear()
            MessageList.getItems.addAll(listMessage.getItems)
          }
        })
      }
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