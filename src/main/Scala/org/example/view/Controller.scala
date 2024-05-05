package org.example.view

import javafx.fxml.FXML
import javafx.scene.control._


class Controller {
  var url = "main.fxml"
  @FXML protected var messageList: ListView[Message] = null
  @FXML var contactList: ListView[Contacts] = null
  @FXML protected var send: Button = null
  @FXML protected var window1: SplitPane = null
  @FXML protected var postText: TextArea = null
  @FXML protected var nick: TextField = null
  @FXML protected var login: Button = null

  @FXML private[example] def initialize(): Unit = {
    assert(messageList != null, "fx:id=\"MessageList\" was not injected: check your FXML file 'first.fxml'.")
    assert(send != null, "fx:id=\"Send\" was not injected: check your FXML file 'first.fxml'.")
    assert(postText != null, "fx:id=\"PostText\" was not injected: check your FXML file 'first.fxml'.")
  }
}
