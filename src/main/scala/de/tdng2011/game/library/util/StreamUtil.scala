package de.tdng2011.game.library.util

import java.io.InputStream
import java.nio.ByteBuffer

/**
 * Created by IntelliJ IDEA.
 * User: benjamin
 * Date: 25.01.11
 * Time: 21:26
 * To change this template use File | Settings | File Templates.
 */

object StreamUtil {

  def read(stream : InputStream, count : Int) : ByteBuffer = {
    while (stream.available < count) {
      Thread.sleep(2)
    }
    val byteArray = new Array[Byte](count)
    stream.read(byteArray)
    ByteBuffer.wrap(byteArray)
  }
}