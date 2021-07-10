package org.example

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.receptionist.ServiceKey
import akka.actor.typed.scaladsl.Behaviors
//import sample.cluster.CborSerializable

//#worker
object Worker {

  val WorkerServiceKey: ServiceKey[Command] = ServiceKey[Worker.Command]("Worker")
  var Name = new String
  sealed trait Command
  final case class TransformText(text: String, replyTo: ActorRef[TextTransformed]) extends Command with CborSerializable
  final case class TextTransformed(text: String) extends CborSerializable
  final case class NewName(name: String) extends CborSerializable with Command
  final case class MyNameAsk( replyTo: ActorRef[MyName])extends Command with CborSerializable
  final case class MyName(name:String)extends CborSerializable
  def apply(): Behavior[Command] =
    Behaviors.setup { ctx =>
      // each worker registers themselves with the receptionist
      ctx.log.info("Registering myself with receptionist")

      ctx.system.receptionist ! Receptionist.Register(WorkerServiceKey, ctx.self)

      Behaviors.receiveMessage {
        /*case TransformText(text, replyTo) =>
          ctx.log.info("hi, i do UpperCase this text ("+text+") from - "+replyTo.path.toString)
          replyTo ! TextTransformed(text.toUpperCase)
          Behaviors.same*/
        case NewName(name) =>
          Name = name
          Behaviors.same
        case MyNameAsk(replyTo) =>
          replyTo ! MyName(Name)
          Behaviors.same
      }
    }
}



//#worker