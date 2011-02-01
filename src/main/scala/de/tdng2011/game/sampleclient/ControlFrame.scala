package de.tdng2011.game.sampleclient

import swing.event.ButtonClicked
import swing.{ToggleButton, FlowPanel, MainFrame}

class ControlFrame(val client : Client) extends MainFrame {
  title = "Client ID: " + client.publicId

  val leftButton : ToggleButton = new ToggleButton("turnLeft") {
    reactions += {case x:ButtonClicked => sendAction}
  }
  val rightButton : ToggleButton = new ToggleButton("turnRight") {
    reactions += {case x:ButtonClicked => sendAction}
  }
  val thrustButton : ToggleButton = new ToggleButton("thrust") {
    reactions += {case x:ButtonClicked => sendAction}
  }
  val fireButton : ToggleButton = new ToggleButton("fire") {
    reactions += {case x:ButtonClicked => sendAction}
  }
  contents = new FlowPanel(leftButton, rightButton, thrustButton, fireButton)

  peer.setLocationRelativeTo(null)
  visible = true

  def sendAction = client !! PlayerActionMessage (leftButton.selected, rightButton.selected, thrustButton.selected, fireButton.selected)
}