package de.tdng2011.game.sampleclient

import de.tdng2011.game.library.util.{ByteUtil,Vec2}
import actors.Actor
import de.tdng2011.game.library.connection.{RelationTypes, AbstractClient}
import de.tdng2011.game.library.{ScoreBoard, World, EntityTypes,Player}
import scala.math.{asin,atan,abs}

class Client(hostname : String,myName:String) extends AbstractClient(hostname, RelationTypes.Player) {
	val worldSize=1000
	val minDistFactor=5

	override def name = myName

	var shotSpeed=10
	var shotRadius=1
	var lastNextPos=Vec2(0,0)
	var lastCanShoot=true
	var lastNextPublicId:Long=0
	var blackList:Long=0

	def norm(v:Vec2)={ 
		val l=v.length.floatValue 
		if (l==0) Vec2(1,0) else Vec2(v.x/l,v.y/l) 
	}

	def fixTurnAround(a:Vec2, b:Vec2) = {
		def minFix(me:Float, you:Float) = if (me<you) me+worldSize else me
		def near(a:Float,b:Float) = abs(a-b) < worldSize/2

		def fixX( t:(Vec2,Vec2) ) = 
			if ( near(t._1.x,t._2.x) ) 
				(t._1,t._2) 
			else 
				( Vec2(minFix(t._1.x,t._2.x) , t._1.y) , Vec2(minFix(t._2.x,t._1.x) , t._2.y) )

		def fixY( t:(Vec2,Vec2) ) = 
			if ( near(t._1.y,t._2.y) ) 
				(t._1,t._2) 
			else 
				( Vec2(t._1.x , minFix(t._1.y,t._2.y)) , Vec2(t._2.x , minFix(t._2.y,t._1.y)) )

		fixX(fixY( (a,b) ))
	}
	
	def dist(a:Player, b:Player) = {
		val (apos,bpos)=fixTurnAround(a.pos,b.pos)
		(apos - bpos).length
	}

	def getNext(self:Player)(a:Player,b:Player) = 
		if (a==self) b else 
		if (b==self) a else 
		/*
		if (a.publicId==blackList) b else 
		if (b.publicId==blackList) a else 
		*/
		if (dist(self,a) < dist(self,b)) a else b

	def findNextPlayer(world:World,self:Player) = world.players.reduceRight(getNext(self))

	def radiantToVec2(radiant:Float) = norm(Vec2(1,0).rotate(radiant))

	def processWorld(world : World) {
		val selfOpt=world.players.find(_.publicId==getPublicId)
		if (selfOpt.isEmpty) return

		val shotOpt = world.shots.find(_.parentId==getPublicId)
		val canShoot = shotOpt.isEmpty

		if (!canShoot) {
			shotSpeed=shotOpt.get.speed
			shotRadius=shotOpt.get.radius
		}

		if (lastCanShoot && !canShoot) blackList=lastNextPublicId

		val self      =selfOpt.get
		val next      =findNextPlayer(world,self)

		val nowPos    =next.pos
		val nowDir    =radiantToVec2(next.direction)

		val (nowNextPos,nowSelfPos) = fixTurnAround(next.pos,self.pos)
		val nowDist=(nowSelfPos-nowNextPos).length

		val eta=nowDist/shotSpeed

		val targetPosAtEta=nowPos + nowDir*(eta.floatValue * next.speed * (if (next.thrust) 1 else 0)) 

		val (targetPos,selfPos) = fixTurnAround(targetPosAtEta,self.pos)
		val dist      =(selfPos-targetPos).length

		val selfDir   =radiantToVec2(self.direction)
		val delta     =norm(targetPos-selfPos)

		val cross     =selfDir.cross(delta)
		val alpha	  =abs(asin(cross))
		val beta		  =atan((next.radius+shotRadius)/dist)
		val aim       =alpha<beta 

		val shoot     =aim && canShoot

		val turn      =(!shoot && !aim)
		val left		  =cross<0  && turn
		val right     =cross>0 && turn

		val ahead	  =dist>self.radius*minDistFactor

		lastNextPublicId=next.publicId
		lastNextPos=next.pos
		lastCanShoot=canShoot

		action(left,right,ahead,shoot)
	}
}
