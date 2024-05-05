package org.example

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior, Scheduler}
import akka.util.Timeout
import javafx.application.Platform
import javafx.scene.control.ListView
import org.example.Frontend._
import org.example.Worker._
import org.example.view.{Contacts, ControllerScala, Message}

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration._

//#frontend
class Frontend(scala: ControllerScala, port: String) {

  private var myWorker: ActorRef[Worker.Command] = null
  private var mapName2Worker = mutable.Map[String, ActorRef[Worker.Command]]()
  private var firstTime = true
  private var myWorkerName = port

  def getActor: Behavior[Event] = Behaviors.setup { ctx =>
    myWorker = ctx.spawn(Worker(), "Worker")
    myWorker.tell(NewName(myWorkerName))
    mapName2Worker(myWorkerName) = myWorker
    val subscriptionAdapter = ctx.messageAdapter[Receptionist.Listing] {
      case Worker.WorkerServiceKey.Listing(workers) =>
        WorkersUpdated(workers)
    }
    ctx.system.receptionist ! Receptionist.Subscribe(Worker.WorkerServiceKey, subscriptionAdapter)

    running(ctx, IndexedSeq.empty, jobCounter = 0)
  }

  private def running(ctx: ActorContext[Event], workers: IndexedSeq[ActorRef[Worker.Command]], jobCounter: Int): Behavior[Event] =

    Behaviors.receiveMessage {
      case WorkersUpdated(newWorkers) =>
        ctx.log.info("List of services registered with the receptionist changed: {}", newWorkers)
        scala.updateListUser()
        running(ctx, newWorkers.toIndexedSeq, jobCounter)

      case NewNameF(wName) =>
        mapName2Worker(wName) = mapName2Worker(myWorkerName)
        mapName2Worker.remove(myWorkerName)
        myWorkerName = wName
        myWorker.tell(NewName(wName))
        Behaviors.same

      case Die() =>
        myWorker ! DieW()
        Behaviors.stopped

      case SendMessageF(message) =>
        if (!message.To.map(_.getName).contains("\u041e\u0431\u0449\u0438\u0439\u0020\u0447\u0430\u0442")) {
          mapName2Worker(message.From.map(_.getName).getOrElse("")).tell(SendMessage(message))
          if (message.From != message.To) {
            mapName2Worker(message.To.map(_.getName).getOrElse("")).tell(SendMessage(message))
          }
        }
        else {
          for (i <- mapName2Worker) {
            i._2.tell(SendMessage(message))
          }
        }
        Behaviors.same

      case UpdateInformation =>
        implicit val scheduler: Scheduler = ctx.system.scheduler
        implicit val timeout: Timeout = 5.seconds
        val newMapNameRef = mutable.Map[String, ActorRef[Worker.Command]]()
        var VecView = Vector[Contacts]()
        for (a <- workers) {
          val b = Await.result(a.ask(MyNameAsk), 10.seconds).name
          newMapNameRef(b) = a
          val pr = a == myWorker
          val buf = new Contacts(a.path.toString, b, pr)
          VecView = VecView :+ buf
        }

        if (newMapNameRef != mapName2Worker || firstTime) {
          mapName2Worker = newMapNameRef
          firstTime = false
          VecView.sortWith((x, y) => x.compare(y))
          val buf = new ListView[Contacts]()
          for (i <- VecView) {
            buf.getItems.add(i)
          }
          Platform.runLater(new Runnable() {
            override def run(): Unit = {
              scala.updateContactsList(buf)
            }
          })
        }
        Behaviors.same


      case UpdateMessage(name) =>
        implicit val scheduler: Scheduler = ctx.system.scheduler
        implicit val timeout: Timeout = 5.seconds
        val b = Await.result(myWorker.ask(ChatWithThisParentAsk(name, _)), 10.seconds).chat
        scala.updateMessages(b)
        Behaviors.same
    }
}

object Frontend {

  sealed trait Event

  //private case object Tick extends Event

  private final case class WorkersUpdated(newWorkers: Set[ActorRef[Worker.Command]]) extends Event

  final case object UpdateInformation extends Event with CborSerializable

  final case class UpdateMessage(name: String) extends Event with CborSerializable

  final case class NewNameF(wName: String) extends Event

  final case class Die() extends Event

  final case class SendMessageF(message: Message) extends Event
}

//#frontend