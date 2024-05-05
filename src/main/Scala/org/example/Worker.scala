package org.example

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import org.example.view.Message

import scala.collection.mutable


//#worker
object Worker {

  val WorkerServiceKey: ServiceKey[Command] = ServiceKey[Worker.Command]("Worker")
  var Name = new String
  private val mapNameMassages = mutable.Map[String, List[Message]]()

  sealed trait Command


  final case class NewName(name: String) extends CborSerializable with Command

  final case class MyNameAsk(replyTo: ActorRef[MyName]) extends Command with CborSerializable

  final case class MyName(name: String) extends CborSerializable

  final case class ChatWithThisParentAsk(name: String, replyTo: ActorRef[ChatWithThisParent]) extends Command with CborSerializable

  final case class ChatWithThisParent(chat: List[Message]) extends CborSerializable

  final case class SendMessage(message: Message) extends Command with CborSerializable

  final case class DieW() extends Command

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

          val partner: String = if (!message.To.map(_.getName).contains(Name)) {
            message.To.map(_.getName).getOrElse("")
          }
          else {
            message.From.map(_.getName).getOrElse("")
          }
          if (mapNameMassages.contains(partner)) {
            mapNameMassages(partner) = mapNameMassages(partner) :+ message

          } else
            mapNameMassages(partner) = List(message)
          Behaviors.same


        case ChatWithThisParentAsk(name, replyTo) =>
          if (mapNameMassages.contains(name)) {
            replyTo ! ChatWithThisParent(mapNameMassages(name))
            Behaviors.same
          }
          else {
            replyTo ! ChatWithThisParent(List(Message()))
            Behaviors.same
          }

        case DieW() =>
          Behaviors.stopped
      }
    }
}
