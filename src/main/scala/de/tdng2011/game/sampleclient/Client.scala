package de.tdng2011.game.sampleclient

import java.net.Socket
import java.io.DataInputStream
import de.tdng2011.game.library.util.{ByteUtil, StreamUtil}
import de.tdng2011.game.library.{EntityTypes, Player, Shot}
import actors.Actor

case class Client(hostname : String) extends Actor with Runnable {

  val name = "123456789012"
  var publicId : Long = 0L

  var entityList = List[Any]()

  private val connection : Socket = connect()
  handshakePlayer()
  new Thread(this).start
  start

  def run() {
    val iStream = new DataInputStream(connection.getInputStream)

    while(true){
      val buf = StreamUtil.read(iStream, 2)
      val id = buf.getShort

      id match {
        case x if x == EntityTypes.Player.id => entityList = entityList :+ new Player(iStream)
        case x if x == EntityTypes.Shot.id   => entityList = entityList :+ new Shot(iStream)
        case x if x == EntityTypes.World.id  => {
          // TODO: do something
          entityList = List[Any]()
        }
        case x => {
          println("barbra streisand! (unknown bytes, wth?!) typeId: " + id)
          System exit -1
        }
      }
    }
  }

  def act = {
    loop{
      react{
        case x : PlayerActionMessage => {
          connection.getOutputStream.write(ByteUtil.toByteArray(x.turnLeft, x.turnRight, x.thrust, x.fire))
        }

        case barbraStreisand => {
          println("[client] wuhuhuhu barbra streisand: " + barbraStreisand)
        }
      }
    }
  }

  private def handshakePlayer() =  {
    connection.getOutputStream.write(ByteUtil.toByteArray(0.shortValue, name))
    val response = StreamUtil.read(new DataInputStream(connection.getInputStream), 9)
    println("response code: " + response.get)
    publicId = response.getLong
  }

  private def connect() : Socket = {
    try {
      new Socket(hostname, 1337)
    } catch {
      case e => {
        println("connecting failed. retrying in 5 seconds");
        Thread.sleep(5000)
        connect()
      }
    }
  }
}