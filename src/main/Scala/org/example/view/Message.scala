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
  var iPostman =true


  def this(postText: String, from: String, to: String, postman: Boolean) {
    this()
    From = from
    To = to
    iPostman = postman
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


  def getPostman:Boolean =iPostman

  def getPostText: String = PostText

  def postTextProperty: SimpleStringProperty = new SimpleStringProperty(PostText)

  def setPostText(postText: String): Unit = {
    PostText=postText
  }

  override def toString: String = {
    From+" : "+ To +" | " +PostText
  }
/*  override def identifier: Int = 12345678

 override def toBinary(o: AnyRef): Array[Byte] =  o match {
   case SerPostText(name)  =>
     name.getBytes(UTF_8)
   case SerFrom(name) =>
     name.getBytes(UTF_8)
   case SerTo(name) =>
     name.getBytes(UTF_8)
   /*case SerPostman(name) =>
     name*/
   case _ =>
     throw new IllegalArgumentException(s"Can't serialize object of type ${o.getClass} in [${getClass.getName}]")
 }

  override def includeManifest: Boolean = true

  def manifest(obj: AnyRef): String =
    obj match {
      case _: SerPostText => PostText
      case _: SerFrom     => From
      case _: SerTo     => To
    }

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    manifest match {
      case PostText =>
        SerPostText(new String(bytes, UTF_8))

      case PongManifest =>
        PingService.Pong
      case _ =>
        throw new IllegalArgumentException(s"Unknown manifest [$manifest]")
    }
  }*/
}

case class SerPostText(name: String)

case class SerFrom(name: String)

case class SerTo(name: String)

case class SerPostman(name: Boolean)
