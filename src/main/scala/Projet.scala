package upmc.akka.leader

import com.typesafe.config.ConfigFactory
import akka.actor._

case class Terminal(id: Int, ip: String, port: Int)
case class Start()

object Projet {
     def main(args: Array[String]) {
          // Error handling
          if (args.size != 1) {
               println("Erreur de syntaxe : run <num>")
               sys.exit(1)
          }

          val id: Int = args(0).toInt

          if (id < 0 || id > 3) {
               println("Erreur : <num> doit etre compris entre 0 et 3")
               sys.exit(1)
          }

          var musicienlist = List[Terminal]()

          // Get addresses of all musicians
          for (i <- 3 to 0 by -1) {
               val address = ConfigFactory.load().getConfig("system" + i).getValue("akka.remote.netty.tcp.hostname").render().replaceAll("\"", "")
               val port = ConfigFactory.load().getConfig("system" + i).getValue("akka.remote.netty.tcp.port").render().replaceAll("\"", "")
               musicienlist = Terminal(i, address, port.toInt) :: musicienlist
          }

          println(musicienlist)

          // Initialize node <id>
          val system = ActorSystem("MozartSystem" + id, ConfigFactory.load().getConfig("system" + id))
          val musicien = system.actorOf(Props(new Musicien(id, musicienlist)), "Musicien" + id)

          musicien ! Start
     }
}

