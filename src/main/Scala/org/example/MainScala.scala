
package org.example
import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.typed.Cluster
import com.typesafe.config.ConfigFactory
object MainScala extends App{

  // starting 2 frontend nodes and 3 backend nodes
    //startup("backend", 25251)
    startup("backend", 25252)
    //startup("frontend", 25252)
    //startup("frontend", 0)
    //startup("frontend", 0)

  object RootBehavior {
    def apply(): Behavior[Nothing] = Behaviors.setup[Nothing] { ctx =>
      val cluster = Cluster(ctx.system)

      if (cluster.selfMember.hasRole("backend")) {
        val workersPerNode =
         // ctx.system.settings.config.getInt("transformation.workers-per-node")

          ctx.spawn(Worker(), "Worker")

      }
      if (cluster.selfMember.hasRole("frontend")) {
        ctx.spawn(Frontend(), "Frontend")
      }
      Behaviors.empty
    }
  }
/*
  //def SetNick(getText: String) = NickName=getText
  val Address="akka://system@127.0.0.1:2511"
  val system = ActorSystem("system")
  private var NickName : String = "";
  val clusterListener = system.actorOf(Props[ClusterActor], "ClusterActor")
  //val sessionManager = system.actorOf(Props[Messenger], "Messenger")

  def getClusterN = clusterListener ;

  Application.launch(classOf[ScalaWindow])
*/

  def startup(role: String, port: Int): Unit = {
    // Override the configuration of the port and role
    val config = ConfigFactory
      .parseString(s"""
        akka.remote.artery.canonical.port=$port
        akka.cluster.roles = [$role]
        """)
      // я не понимаю почему надо писать (akka.actor.provider = cluster) оно уже указано в config ¯\_(ツ)_/¯
      .withFallback(ConfigFactory.load(ConfigFactory.load()))

    ActorSystem[Nothing](RootBehavior(), "system", config)

  }

}
