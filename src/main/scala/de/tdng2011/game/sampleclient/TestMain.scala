package de.tdng2011.game.sampleclient

import de.tdng2011.game.library.util.ByteUtil

/**
 * Created by IntelliJ IDEA.
 * User: benjamin
 * Date: 29.01.11
 * Time: 02:31
 * To change this template use File | Settings | File Templates.
 */

object TestMain {
  def main(args : Array[String]) {
    println("123456789012".toArray.map(x => println(ByteUtil.toByteArray(x).size)))
    println("1234567890".size)
  }
}