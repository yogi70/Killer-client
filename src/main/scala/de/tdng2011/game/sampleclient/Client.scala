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
     val connection = new Socket("localhost",1337);
     while(true){
       val byteArray = new Array[Byte](2)
       connection.getInputStream.read(byteArray)
       val buf = ByteBuffer.wrap(byteArray)

       val id = buf.getShort

       if(id == playerType) {
         entityList = new Player(connection.getInputStream) :: entityList
       } else if (id == shotType) {
         entityList = new Shot(connection.getInputStream) :: entityList
       } else if(id == worldType) {
         Visualizer !! entityList
         entityList = List[Any]()
       } else {
         println("barbra streisand! (unknown bytes, wth?!)")
         System.exit(-1)
       }

     }
  }
}
