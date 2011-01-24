import java.net.Socket
import java.nio.ByteBuffer

object Client {
  def main(args : Array[String]){
     val connection = new Socket("localhost",1337);
     while(true){
       val byteArray = new Array[Byte](56)
       connection.getInputStream.read(byteArray)
       val buf = ByteBuffer.wrap(byteArray)
       println("" +
         "TypeID: "     + buf.getInt() + " " +
         "PublicID: "   + buf.getLong + " " +
         "Pos.X: "      + buf.getDouble + " " +
         "Pos.Y: "      + buf.getDouble + " " +
         "Direction: "  + buf.getDouble + " " +
         "Radius: "     + buf.getInt + " " +
         "Speed: "      + buf.getInt + " " +
         "rotSpeed: "   + buf.getDouble + " " +
         "turnLeft: "   + buf.get + " " +
         "turnRight: "  + buf.get + " " +
         "thrust: "     + buf.get + " " +
         "fire: "       + buf.get)

       /*
type id (player) : int => 4 byte
	publicId : long => 8 byte
	pos.x : Double => 8 byte
	pos.y : Double => 8 byte
	direction : Double => 8 byte
	radius : int => 4 byte
	speed : int => 4 byte
	rotSpeed : Double => 8 byte
	turnLeft : Boolean => Byte => 1 byte
	turnRight : Boolean => Byte => 1 byte
	thrust : Boolean => Byte => 1 byte
	fire : Boolean => Byte => 1 Byte


        */
     }
  }
}
