package org.example.view

import akka.serialization.Serializer
import javafx.beans.property.SimpleStringProperty
import sun.nio.cs.UTF_8
@SerialVersionUID(100L)
class Message() extends Serializable {
  var PostText: String = null
  PostText = "\u0412\u0432\u0435\u0434\u0438\u0442\u0435\u0020\u0442\u0435\u043a\u0441\u0442\u0020\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f"
  private var From: String = null
  private var To: String = null



  def this(postText: String, from: String, to: String) {
    this()
    From = from
    To = to
    PostText =postText
  }


  def setTo(value: String): Unit = {
    To = value
  }
  def getTo: String = To


  def setFrom(value: String): Unit = {
    From = value
  }
  def getFrom: String = From


  def getPostText: String = PostText

  def postTextProperty: SimpleStringProperty = new SimpleStringProperty(PostText)

  def setPostText(postText: String): Unit = {
    PostText=postText
  }

  override def toString: String = {
    From+" : "+ To +" | " +PostText
  }

}
