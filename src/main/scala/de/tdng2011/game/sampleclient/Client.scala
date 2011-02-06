package de.tdng2011.game.sampleclient

import de.tdng2011.game.library.util.ByteUtil
import de.tdng2011.game.library.EntityTypes
import actors.Actor
import de.tdng2011.game.library.connection.{RelationTypes, AbstractClient}

class Client(hostname : String) extends AbstractClient(hostname, RelationTypes.Player) with Actor {

  override def name = "123456789012"

  start

  def act = {
    loop{
      react{
        case x : PlayerActionMessage => {
          getConnection.getOutputStream.write(ByteUtil.toByteArray(EntityTypes.Action, x.turnLeft, x.turnRight, x.thrust, x.fire))
        }

        case barbraStreisand => {
          println("[client] wuhuhuhu barbra streisand: " + barbraStreisand)
        }
      }
    }
  }

  def processFrame(fame : List[Any]) {}
}