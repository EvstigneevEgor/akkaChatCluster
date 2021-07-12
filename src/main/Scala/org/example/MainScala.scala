
package org.example

//import akka.actor.Status.{Failure, Success}

import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, scaladsl}
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.typed.Cluster
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import javafx.application.Application
import javafx.scene.control.ListView
import org.example.Frontend.{NewNameF, SendMessageF, UpdateInformations, UpdateMessage}
import org.example.view.{Contacts, Message, ScalaWindow}

import scala.concurrent.Await
import scala.util.Failure
import scala.util.Success
import scala.concurrent.duration.DurationInt
import scala.reflect.ClassManifestFactory.Nothing

object MainScala extends App {
  implicit val timeout: Timeout = 5.seconds


  val LocalPort = ConfigFactory.load().getInt("MyPort")
  println(LocalPort)

  startup("frontend", LocalPort)
  var frontend: ActorRef[Frontend.Event] = null
  var context: scaladsl.ActorContext[Nothing] = null

  object RootBehavior {
    def apply(): Behavior[Nothing] = Behaviors.setup[Nothing] { ctx =>
      val cluster = Cluster(ctx.system)
      context = ctx
      if (cluster.selfMember.hasRole("backend")) {
        val workersPerNode =
        // ctx.system.settings.config.getInt("transformation.workers-per-node")

          ctx.spawn(Worker(), "Worker")

      }
      if (cluster.selfMember.hasRole("frontend")) {
        frontend = ctx.spawn(Frontend(), "Frontend")
      }
      Behaviors.empty
    }
  }


  Application.launch(classOf[ScalaWindow])


  def startup(role: String, port: Int): Unit = {
    // Override the configuration of the port and role

    val config = ConfigFactory
      .parseString(
        s"""
        akka.remote.artery.canonical.port=$port
        akka.cluster.roles = [$role]
        """)
      // ¯\_(ツ)_/¯
      .withFallback(ConfigFactory.load(ConfigFactory.load()))
    ActorSystem[Nothing](RootBehavior(), "system", config)

  }

  def updateListUser: ListView[Contacts] = {
    implicit val scheduler = context.system.scheduler
    var d = frontend.ask(UpdateInformations(_))
    var dd = Await.result(d, 10.second)
    //println(dd.toString)
    dd.WorkersName
  }

  def reName(newName: String): Unit = {
    frontend.tell(NewNameF(newName))
  }

  def sendMessage(message: Message): Unit = {
    frontend.tell(SendMessageF(message))
  }

  def updateListMessage(name:String): List[Message] = {
    implicit val scheduler = context.system.scheduler
    var d = frontend.ask(UpdateMessage(name,_))
    var dd = Await.result(d, 10.second)
    //println(dd.toString)
    dd.Chat
  }

}
