package de.tdng2011.game.sampleclient

import de.tdng2011.game.library.util.{ByteUtil,Vec2}
import actors.Actor
import de.tdng2011.game.library.connection.{RelationTypes, AbstractClient}
import de.tdng2011.game.library.{ScoreBoard, World, EntityTypes,Player}
import scala.math.{asin,atan,abs}

class Client(hostname : String,myName:String) extends AbstractClient(hostname, RelationTypes.Player) {
  val worldSize=1000

  override def name = myName

  var shotSpeed=10
  var lastNextPos=new Vec2(0,0)
  var lastCanShoot=true
  var lastTarget:Long=0
  var blackList:Long=0

  def norm(v:Vec2)=if (v.length==0) new Vec2(1,0) else new Vec2(v.x/v.length.floatValue,v.y/v.length.floatValue)

  def fix(a:Vec2, b:Vec2) = {
  	var ax=a.x
	var ay=a.y
	var bx=b.x
	var by=b.y

	if (abs(ax-bx)>worldSize/2)
		if (ax<bx)
			ax=ax+worldSize
		else
			bx=bx+worldSize

	if (abs(ay-by)>worldSize/2)
		if (ay<by)
			ay=ay+worldSize
		else
			by=by+worldSize

	(new Vec2(ax,ay), new Vec2(bx,by))
  }

  def dist(a:Player, b:Player) = {
		val (apos,bpos)=fix(a.pos,b.pos)
		(apos - bpos).length
  }

  def getNext(self:Player)(a:Player,b:Player) = if (a==self) b else if (b==self) a else if (a.publicId==blackList) b else if (b.publicId==blackList) a else if (dist(self,a) < dist(self,b)) a else b
  def findNextPlayer(world:World,self:Player) = world.players.reduceRight(getNext(self))

  def processWorld(world : World) {
	 if (world.players.find(_.publicId==getPublicId).isEmpty) return

	 val canShoot  =world.shots.find(_.parentId==getPublicId).isEmpty

	 if (!canShoot) {
	 	val shot=world.shots.find(_.parentId==getPublicId).get
		shotSpeed=shot.speed
	 }

	 if (lastCanShoot && !canShoot) 
	 	blackList=lastTarget

	 val self      =world.players.find(_.publicId==getPublicId).get
	 var next      =findNextPlayer(world,self)

	 val nowPos =next.pos
	 val nowDir =norm(new Vec2(1,0).rotate(next.direction))

	 val (nowNextPos,nowSelfPos) = fix(next.pos,self.pos)
	 val nowDist=(nowSelfPos-nowNextPos).length

	 val eta=nowDist/shotSpeed

	 val thenPos=if (lastNextPos==next.pos) 
							nowPos
						 else
							nowPos+nowDir*(eta.floatValue*next.speed)
	 	

	 val (nextPos,selfPos) = fix(thenPos,self.pos)

	 val dir       =norm(new Vec2(1,0).rotate(self.direction))
	 val delta     =norm(nextPos-selfPos)
	 val cross     =dir.cross(delta)
	 val alpha		=abs(asin(cross))
	 val dist      =(selfPos-nextPos).length
	 val beta		=atan(next.radius/dist)

	 val aim       =alpha<beta 
	 val shoot     =aim && canShoot
	 val ahead		=dist>self.radius*3

	 val turn      =(!shoot && !aim)

	 val left		=cross<0  && turn
	 val right     =cross>=0 && turn

//println("alpha: "+alpha+" shoot: "+shoot+" dist="+dist+" turn: "+turn+" aim: "+aim+"   "+(if (left) "LEFT" else if (right) "RIGHT" else "AHEAD")+" cross="+cross+" id="+next.publicId)
/*println(" date: "+java.lang.System.currentTimeMillis+
		  " cross: "+cross+
        " myPos: "+self.pos.x+","+self.pos.y+
		  " yoPos: "+next.pos.x+","+self.pos.y+
		  " delta: "+delta.x+","+delta.y+
		  " dir:   "+dir.x+","+dir.y+
		  " direction: "+self.direction)
		  */
//println("left: "+left+" right: "+right+" dir: "+self.direction)
//println("shoot: "+shoot+" canShoot: "+canShoot)

	 lastTarget=next.publicId
	 lastNextPos=next.pos
	 lastCanShoot=canShoot

    action(left,right,ahead,shoot)
  }
}
