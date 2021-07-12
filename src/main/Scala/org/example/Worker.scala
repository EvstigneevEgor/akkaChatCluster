package org.example

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.receptionist.ServiceKey
import akka.actor.typed.scaladsl.Behaviors
import org.example.view.Message

import scala.collection.mutable


//#worker
object Worker {

  val WorkerServiceKey: ServiceKey[Command] = ServiceKey[Worker.Command]("Worker")
  var Name = new String
  var mapNameMassenges = mutable.Map[String, List[Message]]()
  sealed trait Command


  final case class NewName(name: String) extends CborSerializable with Command

  final case class MyNameAsk(replyTo: ActorRef[MyName]) extends Command with CborSerializable

  final case class MyName(name: String) extends CborSerializable

  final case class ChatWithThisParentAsk(name:String ,replyTo: ActorRef[ChatWithThisParent]) extends Command with CborSerializable

  final case class ChatWithThisParent(chat: List[Message])extends  CborSerializable

  final case class SendMessage(message: Message) extends Command with CborSerializable

  def apply(): Behavior[Command] =
    Behaviors.setup { ctx =>
      // each worker registers themselves with the receptionist
      ctx.log.info("Registering myself with receptionist")

      ctx.system.receptionist ! Receptionist.Register(WorkerServiceKey, ctx.self)

      Behaviors.receiveMessage {

        case NewName(name) =>
          Name = name
          Behaviors.same
        case MyNameAsk(replyTo) =>
          replyTo ! MyName(Name)
          Behaviors.same
        case SendMessage(message) =>

          val partner: String = if (message.getTo != Name) {
            message.getTo
          }
          else {
            message.getFrom
          }
          println("/*-*/-*/-*/-*/-*/-*/    "+partner)
          //mapNameMassenges(partner) = mapNameMassenges.get(partner).fold(List(message))((mapNameMassenges(partner) :+ message))
          if(mapNameMassenges.contains(partner)) {
            println("new")
            mapNameMassenges(partner) = mapNameMassenges(partner) :+ message

          }else
            mapNameMassenges(partner)=List(message)
          println(mapNameMassenges(partner))
          Behaviors.same


        case ChatWithThisParentAsk(name,replyTo) =>
          if(mapNameMassenges.contains(name))
            {
              println("121212121212",mapNameMassenges(name))
              replyTo ! ChatWithThisParent(mapNameMassenges(name))
              Behaviors.same
            }
          else{
            replyTo ! ChatWithThisParent(List (new Message()))
          Behaviors.same
          }
      }
    }
}


//#worker