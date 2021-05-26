

package org.example

import akka.actor.{Actor, ActorLogging, ActorRef}
import org.example.view.{GetName, MyName, Rename, SetName}

class Messenger(userID: String) extends Actor with ActorLogging {
  var Adres : String = ""
  var Name : String = ""
  var parrent : ActorRef = null
  override def receive: Receive = {
    case MyName(memberAdres,num)=>
      Adres = memberAdres
      Name = num
      if(parrent==null)
        parrent=sender()

    case GetName() =>
        sender ! Name
    case SetName(name) =>
      parrent ! Rename(Name,name)
      Name=name

  }
}

