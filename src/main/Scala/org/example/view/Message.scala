package org.example.view

import akka.serialization.Serializer
import javafx.beans.property.SimpleStringProperty

class Message() /*extends Serializer*/ {
  var PostText: SimpleStringProperty = null
  this.PostText = new SimpleStringProperty("\u0412\u0432\u0435\u0434\u0438\u0442\u0435\u0020\u0442\u0435\u043a\u0441\u0442\u0020\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f")
  private var From: String = null
  private var To: String = null
  var iPostman =true


  def this(postText: String, from: String, to: String, postman: Boolean) {
    this()
    From = from
    To = to
    iPostman = postman
    this.PostText = new SimpleStringProperty(postText)
  }


  def setTo(value: String): Unit = {
    To = value
  }
  def getTo: String = To


  def setFrom(value: String): Unit = {
    From = value
  }
  def getFrom: String = From


  def getPostman:Boolean =iPostman

  def getPostText: String = PostText.get

  def postTextProperty: SimpleStringProperty = PostText

  def setPostText(postText: String): Unit = {
    this.PostText.set(postText)
  }
/*
  override def identifier: Int =

    

 override def toBinary(o: AnyRef): Array[Byte] = ???

  override def includeManifest: Boolean = ???

  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = ???*/
}
