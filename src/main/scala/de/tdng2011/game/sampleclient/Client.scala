package de.tdng2011.game.sampleclient

import java.net.Socket
import java.io.DataInputStream
import de.tdng2011.game.library.util.{ByteUtil, StreamUtil}
import de.tdng2011.game.library.{EntityTypes, Player, Shot}
import actors.Actor

class Client(hostname : String) extends Actor with Runnable {
  val playerType = EntityTypes.Player.id.shortValue
  val shotType = EntityTypes.Shot.id.shortValue
  val worldType = EntityTypes.World.id.shortValue

  val name = "123456789012"
  var publicId : Long = 0L

  var entityList = List[Any]()

  private val connection : Socket = connect()
  handshakePlayer()
  new Thread(this).start
  start

  def run() {
    val iStream = new DataInputStream(connection.getInputStream)

    while(true) getFrame(iStream)
  }

  def getFrame(iStream : DataInputStream) : List[Any] = StreamUtil.read(iStream, 2).getShort match {
    case `playerType` => new Player(iStream) :: getFrame(iStream)
    case `shotType`   => new Shot(iStream) :: getFrame(iStream)
    case `worldType`  => {
      val size = StreamUtil.read(iStream, 4).getInt
      StreamUtil.read(iStream, size)
      Nil
    }
    case x => {
          println("barbra streisand! (unknown bytes, wth?!) typeId: " + x)
          System.exit(-1)
          Nil // make the compiler happy..
    }
  }

  def act = {
    loop{
      react{
        case x : PlayerActionMessage => {
          connection.getOutputStream.write(ByteUtil.toByteArray(EntityTypes.Action, x.turnLeft, x.turnRight, x.thrust, x.fire))
        }

        case barbraStreisand => {
          println("[client] wuhuhuhu barbra streisand: " + barbraStreisand)
        }
      }
    }
  }

  private def handshakePlayer() =  {
    val iStream = new DataInputStream(connection.getInputStream)
    connection.getOutputStream.write(ByteUtil.toByteArray(EntityTypes.Handshake, 0.shortValue, name))

    val buf    = StreamUtil.read(iStream, 6)
    val typeId = buf.getShort
    val size   = buf.getInt

    val response = StreamUtil.read(iStream, size)
    if (typeId == EntityTypes.Handshake.id) {
      val responseCode = response.get
      println("response code: " + responseCode)
      if (responseCode == 0)
        publicId = response.getLong
    }
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