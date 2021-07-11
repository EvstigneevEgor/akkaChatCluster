package org.example

import scala.concurrent.duration._
import akka.util.Timeout
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import javafx.collections.FXCollections
import javafx.scene.control.ListView
import org.example.MainScala.context
import org.example.Worker.{MyName, MyNameAsk, NewName, SendMessage}
import org.example.view.{Contacts, Message}

import scala.collection.mutable
import scala.{+:, :+}
import scala.concurrent.Await
import scala.util.Failure
import scala.util.Success

//#frontend
object Frontend {

  sealed trait Event

  private case object Tick extends Event

  private final case class WorkersUpdated(newWorkers: Set[ActorRef[Worker.Command]]) extends Event


  final case class UpdateInformations(replyTo: ActorRef[AnswerUpdateInformations]) extends Event with CborSerializable

  final case class AnswerUpdateInformations(WorkersName: ListView[Contacts]) extends CborSerializable

  final case class NewNameF(wName: String) extends Event

  final case class SendMessageF(message: Message) extends Event

  var Wrk: ActorRef[Worker.Command] = null
  var mapNameRef = mutable.Map[String, ActorRef[Worker.Command]]()
  var firstTime = true

  def apply(): Behavior[Event] = Behaviors.setup { ctx =>
    Behaviors.withTimers { timers =>
      // subscribe to available workers
      Wrk = ctx.spawn(Worker(), "Worker")
      Wrk.tell(NewName(MainScala.LocalPort.toString))
      mapNameRef(MainScala.LocalPort.toString) = Wrk

      val subscriptionAdapter = ctx.messageAdapter[Receptionist.Listing] {
        case Worker.WorkerServiceKey.Listing(workers) =>
          WorkersUpdated(workers)
      }
      ctx.system.receptionist ! Receptionist.Subscribe(Worker.WorkerServiceKey, subscriptionAdapter)

      //timers.startTimerWithFixedDelay(Tick, Tick, 2.seconds)

      running(ctx, IndexedSeq.empty, jobCounter = 0)
    }
  }

  private def running(ctx: ActorContext[Event], workers: IndexedSeq[ActorRef[Worker.Command]], jobCounter: Int): Behavior[Event] =

    Behaviors.receiveMessage {
      case WorkersUpdated(newWorkers) =>
        ctx.log.info("List of services registered with the receptionist changed: {}", newWorkers)
        running(ctx, newWorkers.toIndexedSeq, jobCounter)

      case NewNameF(wName) =>
        implicit val scheduler = ctx.system.scheduler
        implicit val timeout: Timeout = 5.seconds

        Wrk.tell(NewName(wName))
        Behaviors.same
      case SendMessageF(message) =>
        ctx.log.info("eeeeeeeeeeeeeeeeee, we send "+message.getFrom+" ; "+message.getTo )
        println(mapNameRef.toString)
        mapNameRef.get(message.getFrom).get.tell(SendMessage(message))
        mapNameRef.get(message.getTo).get.tell(SendMessage(message))
        //Wrk.tell(SendMessage(message))
        Behaviors.same
      case UpdateInformations(replyTo) =>
        implicit val scheduler = ctx.system.scheduler
        implicit val timeout: Timeout = 5.seconds

        var newMapNameRef = mutable.Map[String, ActorRef[Worker.Command]]()
        var VecView = Vector[Contacts]()

        for (a <- workers) {

          val b = Await.result(a.ask(MyNameAsk(_)), 10.seconds).name
          //println("Name - "+b)
          newMapNameRef(b) = a
          val pr = (a == Wrk)
          val buf = (new Contacts(a.path.toString, b, pr))
          println(buf)
          VecView = VecView :+ buf
        }
        if (newMapNameRef != mapNameRef || firstTime) {
          mapNameRef = newMapNameRef
          firstTime = false
          // println("Sort")
          //println(VecView)
          VecView.sortWith((x, y) => x.compare(y))
          //println(VecView)
          var buf = new ListView[Contacts]()
          for (i <- VecView) {
            buf.getItems.add(i)
            println(mapNameRef)
          }
          replyTo ! AnswerUpdateInformations(buf)
        }
        replyTo ! AnswerUpdateInformations(new ListView[Contacts]())
        Behaviors.same

    }
}

//#frontend