package de.tdng2011.game.sampleclient

import java.net.Socket
import java.io.DataInputStream
import de.tdng2011.game.library.util.{ByteUtil, StreamUtil}
import de.tdng2011.game.library.{EntityTypes, Player, Shot}

object Client {
  var entityList = List[Any]()

  val connection : Socket = connect()
  handshakePlayer

  def main(args : Array[String]){
    val stream = new DataInputStream(connection.getInputStream)

    connection.getOutputStream.write(ByteUtil.toByteArray(true, false, true, false))

    while(true){
      val buf = StreamUtil.read(stream, 2)
      val id = buf.getShort

      id match {
        case x if x == EntityTypes.Player.id => entityList = entityList :+ new Player(stream)
        case x if x == EntityTypes.Shot.id   => entityList = entityList :+ new Shot(stream)
        case x if x == EntityTypes.World.id  => {
          entityList = List[Any]()
        }
        case x => {
          println("barbra streisand! (unknown bytes, wth?!) typeId: " + id)
          System exit -1
        }
      }
    }
  }

  def handshakePlayer =  {
    connection.getOutputStream.write(ByteUtil.toByteArray(0.shortValue, "123456789012"))
    val response = StreamUtil.read(new DataInputStream(connection.getInputStream), 9);
    println("response code: " + response.get)
    println("publicId: " + response.getLong)
  };

  def connect() : Socket = {
    try {
      new Socket("localhost",1337)
    } catch {
      case e => {
        println("connecting failed. retrying in 5 seconds");
        Thread.sleep(5000)
        connect()
      }
    }
  }
}