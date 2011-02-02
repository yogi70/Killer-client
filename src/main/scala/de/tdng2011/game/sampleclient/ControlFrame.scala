package de.tdng2011.game.sampleclient

import swing.event.ButtonClicked
import swing.{Reactions, ToggleButton, FlowPanel, MainFrame}

class ControlFrame(val client : Client) extends MainFrame {
  title = "Client ID: " + client.publicId

  val leftButton : ToggleButton = new ToggleButton("turnLeft") {
    reactions += clickReaction
  }
  val rightButton : ToggleButton = new ToggleButton("turnRight") {
    reactions += clickReaction
  }
  val thrustButton : ToggleButton = new ToggleButton("thrust") {
    reactions += clickReaction
  }
  val fireButton : ToggleButton = new ToggleButton("fire") {
    reactions += clickReaction
  }
  contents = new FlowPanel(leftButton, rightButton, thrustButton, fireButton)

  peer.setLocationRelativeTo(null)
  visible = true

  def clickReaction() : Reactions.Reaction = {case x:ButtonClicked => sendAction}
  def sendAction = client !! PlayerActionMessage (leftButton.selected, rightButton.selected, thrustButton.selected, fireButton.selected)
}