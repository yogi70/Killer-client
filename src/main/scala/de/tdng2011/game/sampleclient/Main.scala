package de.tdng2011.game.sampleclient

import swing.event.ButtonClicked
import swing._

object Main extends SwingApplication{
  def startup(args: Array[String]) {
  	 if (args.length<2) 
	 	println("required arguments: name ip")
	 else
		new Client(args(1),args(0))
  }
}
