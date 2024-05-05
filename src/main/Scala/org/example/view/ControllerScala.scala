package org.example.view

import akka.actor.typed.ActorRef
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.control.{ListCell, ListView, SelectionMode, TextFormatter}
import javafx.scene.text.TextAlignment
import javafx.util.StringConverter
import org.example.Frontend._

import java.net.URL
import java.util.ResourceBundle

class ControllerScala extends Controller with Initializable {

  var frontend: Option[ActorRef[Event]] = None

  def setFrontend(fr: ActorRef[Event]): Unit = frontend = Some(fr)

  var partner: SimpleObjectProperty[Contacts] = new SimpleObjectProperty[Contacts]()
  private val myContact: SimpleObjectProperty[Contacts] = new SimpleObjectProperty[Contacts](new Contacts("", "", true))

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    contactList.getSelectionModel.setSelectionMode(SelectionMode.SINGLE)
    contactList.getSelectionModel.selectedItemProperty.addListener(new ChangeListener[Contacts]() {
      override def changed(observable: ObservableValue[_ <: Contacts], oldValue: Contacts, newValue: Contacts): Unit = {
        if (newValue != null) {
          partner.setValue(newValue)
          updateListMessage(newValue.getName) //todo here is bug. Fix that. Address doesn`t work. or somewhere threadRacing
        }
        postText.setPromptText(partner.getValue.Name + " : " + myContact.getName)
      }
    })
    val textFormatter = new TextFormatter[Contacts](new StringConverter[Contacts] {
      override def toString(t: Contacts): String = t.getName

      override def fromString(s: String): Contacts = {
        myContact.getValue.setName(s)
        myContact.getValue
      }
    })
    textFormatter.valueProperty().bindBidirectional(myContact)
    nick.setTextFormatter(textFormatter)
    contactList.setCellFactory(_ => new ListCell[Contacts]() {
      override protected def updateItem(item: Contacts, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (empty || item == null || item.getName == null) setText(null)
        else {
          setText(item.getName)
        }
      }
    })

    messageList.setCellFactory(_ => new ListCell[Message]() {
      override protected def updateItem(item: Message, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (empty || item == null || item.PostText == null) setText(null)
        else {
          if (item.From.isDefined)
            setText(item.From.get + ": " + item.PostText)
          else
            setText(item.PostText)
          setTextAlignment(if (item.From.contains(myContact.getValue)) TextAlignment.LEFT else TextAlignment.RIGHT)
          setAlignment(
            if (item.From.isEmpty)
              Pos.CENTER
            else if (item.From.contains(myContact.getValue))
              Pos.CENTER_RIGHT
            else
              Pos.CENTER_LEFT

          )
        }
      }
    })

    messageList.getItems.add(Message())

    //    messageList.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)

    send.setOnAction(_ => Sender())
    login.setOnAction(_ => Login())
    partner.addListener(new ChangeListener[Contacts]() {
      override def changed(observable: ObservableValue[_ <: Contacts], oldValue: Contacts, newPartnerName: Contacts): Unit = {
        if (newPartnerName != null)
          updateListMessage(newPartnerName.getName)
        updateListUser()
      }
    })
    updateListUser()
    postText.setPromptText("\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435!")
  }


  private def Sender(): Unit = {
    if (!(postText.getText == "")) {
      if (partner != null && myContact != null) {
        val m = Message(postText.getText, Some(myContact.getValue), Some(partner.getValue))
        sendMessage(m)
        if (messageList.getItems.get(0).PostText == Message.presetMessages)
          messageList.getItems.remove(0)
        messageList.getItems.add(m)
        postText.clear()
      }
    }
  }


  private def Login(): Unit = {
    if (!(nick.getText == "")) {
      reName(nick.getText)
      nick.setEditable(false)
    }
  }

  def updateContactsList(newlist: ListView[Contacts]): Unit = {
    if (!newlist.getItems.isEmpty) {
      contactList.getItems.clear()
      contactList.getItems.addAll(newlist.getItems)
      contactList.getItems.add(new Contacts("", "\u041e\u0431\u0449\u0438\u0439\u0020\u0447\u0430\u0442", false))
    }
  }

  def updateListUser(): Unit = {
    frontend.foreach(fr => fr.tell(UpdateInformation))
  }

  private def reName(newName: String): Unit = {
    frontend.foreach(_.tell(NewNameF(newName)))

  }

  private def sendMessage(message: Message): Unit = {
    frontend.foreach(_.tell(SendMessageF(message)))
  }

  private def updateListMessage(name: String): Unit = {
    frontend
      .foreach(fr => fr.tell(UpdateMessage(name)))
  }

  def updateMessages(messages: List[Message]): Unit = {
    val listMessage = new ListView[Message]
    for (i <- messages) {
      listMessage.getItems.add(i)
    }
    if (!listMessage.getItems.isEmpty)
      if (!messageList.getItems.containsAll(listMessage.getItems)) {
        Platform.runLater(new Runnable() {
          override def run(): Unit = {
            messageList.getItems.clear()
            messageList.getItems.addAll(listMessage.getItems)
          }
        })
      }
  }
}