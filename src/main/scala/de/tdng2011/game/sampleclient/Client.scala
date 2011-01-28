package de.tdng2011.game.sampleclient

import java.net.Socket
import de.tdng2011.game.library.{Player, Shot}
import de.tdng2011.game.library.util.StreamUtil
import visual.Visualizer
import java.io.DataInputStream

object Client {
  val playerType = 0
  val shotType = 1
  val worldType = 3

  var entityList = List[Any]()

  Visualizer.start

  def main(args : Array[String]){
    val connection = new Socket("remote.coding4coffee.org",1337);
    //val connection = new Socket("localhost",1337);

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
}
