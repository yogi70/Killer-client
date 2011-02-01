package de.tdng2011.game.sampleclient

import swing.event.ButtonClicked
import swing._

object Main extends SwingApplication{
  def startup(args: Array[String]) {
    val dialog = new MainFrame {
      title = "Connect"
      val inputField = new TextField {
        text = "localhost"
        columns = 20
      }
      val connectionButton = new Button("Connect!") {
        reactions += {
          case x:ButtonClicked => {
            Client(inputField.text) !! PlayerActionMessage(true, false, true, false)
            //close
          }
        }
      }
      defaultButton = connectionButton
      contents = new FlowPanel(inputField, connectionButton)
      peer.setLocationRelativeTo(null)
      visible = true
    }
  }
}