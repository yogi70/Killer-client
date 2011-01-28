package de.tdng2011.game.sampleclient

import java.net.Socket
import de.tdng2011.game.library.{Player, Shot}
import de.tdng2011.game.library.util.StreamUtil
import visual.Visualizer
import java.io.DataInputStream
import java.nio.ByteBuffer
import java.nio.ByteBuffer._

object Client {
  val playerType = 0
  val shotType = 1
  val worldType = 3

  var entityList = List[Any]()

  Visualizer.start
  var connection : Socket = connect()

  def main(args : Array[String]){

    connection.getOutputStream.write(ByteUtil.toByteArray(1.shortValue));


    val stream = new DataInputStream(connection.getInputStream)

    while(true){
      val buf = StreamUtil.read(stream, 2)

      val id = buf.getShort

      if(id == playerType) {
        entityList = new Player(stream) :: entityList
      } else if (id == shotType) {
        entityList = new Shot(stream) :: entityList
      } else if(id == worldType) {
        Visualizer !! entityList
        entityList = List[Any]()
      } else {
        println("barbra streisand! (unknown bytes, wth?!) typeId: " + id)
        System.exit(-1)
      }
    }
  }
  def connect() : Socket = {
    val connection : Socket = try {
      new Socket("localhost",1337)
    } catch {
      case e => {
        println("connecting failed. retrying in 5 seconds");
        Thread.sleep(5000)
        connect()
      }
    }
    connection
  }
}



object ByteUtil {

  def toByteArray(a : Any*) : Array[Byte] = {
    val byteBuffer : ByteBuffer = allocate(a.size*8) // pessimistic size, works if all elements are 8 bytes
    for(x <- a){
      x match {
        case x : Float => byteBuffer.putFloat(x)
        case x : Double => byteBuffer.putDouble(x)
        case x : Long => byteBuffer.putLong(x)
        case x : Int => byteBuffer.putInt(x)
        case x : Short => byteBuffer.putShort(x)
        case x : Char => byteBuffer.putChar(x)
        case x : Byte => byteBuffer.put(x)
        case x : Boolean => byteBuffer.put(if(x) 1.byteValue else 0.byteValue)
        case barbraStreisand => println("error! unknown value, your byte array will not contain " + barbraStreisand)
      }
    }
    val byteArray = new Array[Byte](byteBuffer.position)
    byteBuffer.position(0)
    byteBuffer.get(byteArray)
    byteArray
  }
}