package de.tdng2011.game.library

import java.io.InputStream
import java.nio.ByteBuffer

/**
 * Created by IntelliJ IDEA.
 * User: benjamin
 * Date: 24.01.11
 * Time: 23:47
 * To change this template use File | Settings | File Templates.
 */

class Shot(stream : InputStream) {
  val byteArray = new Array[Byte](52)
  stream.read(byteArray)
  val buf = ByteBuffer.wrap(byteArray)

  val publicId : Long     = buf.getLong
  val pos : Vec2          = Vec2(buf.getDouble, buf.getDouble)
  val direction : Double  = buf.getDouble
  val radius : Int        = buf.getInt
  val speed : Int         = buf.getInt
  val parentId : Long     = buf.getLong
  val lifeTime : Float    = buf.getFloat
}