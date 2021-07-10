package org.example

import scala.concurrent.duration._
import akka.util.Timeout
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import javafx.scene.control.ListView
import org.example.MainScala.context
import org.example.Worker.{MyName, MyNameAsk, NewName, TextTransformed}
import org.example.view.Contacts

import scala.concurrent.Await
import scala.util.Failure
import scala.util.Success

//#frontend
object Frontend {

  sealed trait Event
  private case object Tick extends Event
  private final case class WorkersUpdated(newWorkers: Set[ActorRef[Worker.Command]]) extends Event
  private final case class TransformCompleted(originalText: String, transformedText: String) extends Event
  private final case class JobFailed(why: String, text: String) extends Event
  final case class UpdateInformations( replyTo: ActorRef[AnswerUpdateInformations]) extends Event with CborSerializable
  final case class AnswerUpdateInformations(WorkersName: ListView[Contacts]) extends CborSerializable
  final case class NewNameF(wName: String) extends Event
var Wrk :ActorRef[Worker.Command] = null
  def apply(): Behavior[Event] = Behaviors.setup { ctx =>
    Behaviors.withTimers { timers =>
      // subscribe to available workers
      Wrk = ctx.spawn(Worker(), "Worker")
      Wrk.tell(NewName(MainScala.LocalPort.toString))
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
     /* case Tick =>
        if (workers.isEmpty) {
          ctx.log.warn("Got tick request but no workers available, not sending any work")
          Behaviors.same
        } else {
          // how much time can pass before we consider a request failed
          implicit val timeout: Timeout = 5.seconds

          val selectedWorker = workers(jobCounter % workers.size)
          if(selectedWorker != Wrk){
          ctx.log.info("Sending work for processing to {}", selectedWorker)
          val text = s"hello-$jobCounter"
          ctx.ask(selectedWorker, Worker.TransformText(text, _)) {
            case Success(transformedText) => TransformCompleted(transformedText.text, text)
            case Failure(ex) => JobFailed("Processing timed out", text)
          }
          }else{
            ctx.log.info("Got tick request but no workers available, not sending any work")

          }
          running(ctx, workers, jobCounter + 1)

        }
      case TransformCompleted(originalText, transformedText) =>
        ctx.log.info("Got completed transform of {}: {}", originalText, transformedText)
        Behaviors.same

      case JobFailed(why, text) =>
        ctx.log.warn("Transformation of text {} failed. Because: {}", text, why)
        Behaviors.same*/
      case NewNameF(wName) =>
        Wrk.tell(NewName(wName))
        Behaviors.same
      case UpdateInformations( replyTo) =>
        implicit val scheduler = ctx.system.scheduler
        implicit val timeout: Timeout = 5.seconds
        val listView : ListView[Contacts] =new ListView[Contacts]
        for (a<-workers){
          var b = Await.result(a.ask(MyNameAsk(_)),10.seconds).name
          //println("Name - "+b)
          val pr = (a==Wrk)
          if (pr)
            println(")))))))))))")
          listView.getItems.add(new Contacts(a.path.toString, b,pr))
        }
        replyTo ! AnswerUpdateInformations(listView)
        Behaviors.same

    }
}
//#frontend