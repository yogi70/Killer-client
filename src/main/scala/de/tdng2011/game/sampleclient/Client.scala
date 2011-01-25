package de.tdng2011.game.sampleclient

import java.net.Socket
import java.nio.ByteBuffer
import de.tdng2011.game.library.{Player, Shot}
import visual.Visualizer

object Client {
  val playerType = 0
  val shotType = 1
  val worldType = 3

  var entityList = List[Any]()

  Visualizer.start

  def main(args : Array[String]){
    //val connection = new Socket("remote.coding4coffee.org",1337);
    val connection = new Socket("localhost",1337);
    while(true){
      val byteArray = new Array[Byte](2)
      connection.getInputStream.read(byteArray, 0, 2)
      val buf = ByteBuffer.wrap(byteArray)

      val id = buf.getShort

      if(id == playerType) {
        println("player")
        entityList = new Player(connection.getInputStream) :: entityList
      } else if (id == shotType) {
        println("shot")
        entityList = new Shot(connection.getInputStream) :: entityList
      } else if(id == worldType) {    	  
        Visualizer !! entityList
        println("world")
        entityList = List[Any]()
      } else {
        println("barbra streisand! (unknown bytes, wth?!) typeId: " + id)
        println("current world:")
        for (x <- entityList) {
          println(x)
        }
        System.exit(-1)
      }
    }
  }
}
