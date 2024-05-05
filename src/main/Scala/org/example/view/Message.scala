package org.example.view

import javafx.beans.property.SimpleStringProperty

@SerialVersionUID(100L)
case class Message(PostText: String, From: Option[Contacts], To: Option[Contacts]) extends Serializable {


  def postTextProperty: SimpleStringProperty = new SimpleStringProperty(PostText)

  override def toString: String = {
    From.map(_.name) + " : " + To.map(_.name) + " | " + PostText
  }

}

object Message {
  def apply(): Message =
    Message(PostText = presetMessages, From = None, To = None)

  val presetMessages = "Введите текст сообщения"
}