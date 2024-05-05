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
        if (newValue != null)
          partner.setValue(newValue)
        updateMessagesAndContacts()
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
            setText(item.From.get.getName + ": " + item.PostText)
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
    send.setOnAction { _ =>
      Sender()
      updateMessagesAndContacts()
    }
    login.setOnAction { _ =>
      Login()
      updateMessagesAndContacts()
    }
    partner.addListener(new ChangeListener[Contacts]() {
      override def changed(observable: ObservableValue[_ <: Contacts], oldValue: Contacts, newPartnerName: Contacts): Unit = {
        println("listen " + newPartnerName)
        updateMessagesAndContacts()
      }
    })
    updateListUser()
  }


  private def updateMessagesAndContacts(): Unit = {
    val replyer = partner.getValue
    if (replyer != null) {
      updateListMessage(replyer.getName)
      postText.setPromptText(replyer.name + " : " + myContact.getValue.getName)
    }
    updateListUser()
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
      contactList.getItems.add(new Contacts("", "Общий чат", false))
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

  def setMyContact(contacts: Contacts): Unit = myContact.setValue(contacts)

}