package de.tdng2011.game.killerclient

import de.tdng2011.game.library.util.{ByteUtil,Vec2}
import actors.Actor
import de.tdng2011.game.library.connection.{RelationTypes, AbstractClient}
import de.tdng2011.game.library.{ScoreBoard, World, EntityTypes,Player,Shot}
import scala.math.{asin,atan,abs,acos}

class Client(hostname : String,myName:String) extends AbstractClient(hostname, RelationTypes.Player) {
	val worldSize=1000
	val minDistFactor=5

	override def name = myName

	var shotSpeed=10
	var shotRadius=1
	var lastNextPos=Vec2(0,0)
	var lastCanShoot=true
	var lastNextPublicId:Long=0
	var lastSelfPos=Vec2(0,0)
	var blackList:Long=0
	var isSucker=true

	def norm(v:Vec2)={ 
		val l=v.length.floatValue 
		if (l==0) Vec2(1,0) else Vec2(v.x/l,v.y/l) 
	}

	def orto(v:Vec2) = new Vec2(v.y,-v.x)

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

	def distShotToPlayer(a:Shot, b:Player) = {
		val (apos,bpos)=fixTurnAround(a.pos,b.pos)
		(apos - bpos).length
	}

	def getNext(self:Player)(a:Player,b:Player) = 
		if (a==self) b else 
		if (b==self) a else 
		if (a.publicId==blackList) b else 
		if (b.publicId==blackList) a else 
		if (dist(self,a) < dist(self,b)) a else b

	def getNextShot(self:Player)(a:Shot,b:Shot) = 
		if (a.parentId==self.publicId) b else
		if (b.parentId==self.publicId) a else
		if (distShotToPlayer(a,self) < distShotToPlayer(b,self)) a else b

	def findNextPlayer(world:World,self:Player) = world.players.reduceRight(getNext(self))

	def findNextShot(world:World,self:Player) :Option[Shot]= if (world.shots.length>0) Some(world.shots.reduceRight(getNextShot(self))) else None

	def radiantToVec2(radiant:Float) = norm(Vec2(1,0).rotate(radiant))
	def skalar(a:Vec2,b:Vec2) = a.x*b.x + a.y*b.y

	def calcGamma(s:Vec2, sd:Vec2, o:Vec2, od:Vec2) = 
		( o.y*sd.x - s.y*sd.x + s.x*sd.y - o.x*sd.y) / 
		( od.x*sd.y - od.y*sd.x)


	def processWorld(world : World) {
		val selfOpt=world.players.find(_.publicId==getPublicId)
		if (selfOpt.isEmpty) return

		val shotOpt = world.shots.find(_.parentId==getPublicId)
		val canShoot = shotOpt.isEmpty

		if (!canShoot) {
			shotSpeed=shotOpt.get.speed
			shotRadius=shotOpt.get.radius
		}

		if (lastCanShoot && !canShoot && isSucker) blackList=lastNextPublicId

		val self      =selfOpt.get
		val selfDir   =radiantToVec2(self.direction)
		
		val otherShot = findNextShot(world,self)

		val afraid= if (otherShot.isEmpty) false else {
			val (otherShotPos,selfPos) = fixTurnAround(otherShot.get.pos,self.pos)

			val selfDelta=selfDir*self.speed
			val otherShotDelta=radiantToVec2(otherShot.get.direction)*otherShot.get.speed

			val gamma=calcGamma(selfPos, selfDelta ,otherShotPos, otherShotDelta)

			val collisionPoint=otherShotPos+otherShotDelta*gamma
			val selfCollisionPosition=selfPos+selfDelta*gamma

			(collisionPoint-selfCollisionPosition).length < (self.radius+otherShot.get.radius) *2 && otherShot.get.lifeTime>gamma
		}

		/*
		val next = if (afraid) 
			world.players.find(_.publicId==otherShot.parentId).get
		else
			findNextPlayer(world,self)
		*/
		val next = findNextPlayer(world,self)

		val nowPos    =next.pos
		val nowDir    =radiantToVec2(next.direction)

		val (nowNextPos,nowSelfPos) = fixTurnAround(next.pos,self.pos)
		val nowDist=(nowSelfPos-nowNextPos).length

		val eta=nowDist/shotSpeed

		val targetPosAtEta=nowPos + nowDir*(eta.floatValue * next.speed * (if (next.thrust) 1 else 0)) 

		val (targetPos,selfPos) = fixTurnAround(targetPosAtEta,self.pos)
		val dist      =(selfPos-targetPos).length

		val targetDir =radiantToVec2(next.direction)

		val delta     =norm(targetPos-selfPos)

		val cross     =selfDir.cross(delta)
		val alphaSin  =abs(asin(cross))
		val alphaCos  =abs(acos(skalar(selfDir,delta)))

		val beta		  =atan((next.radius /* +shotRadius */ )/dist)
		val aim       =alphaSin<beta && alphaCos>0

		val shoot     =aim && canShoot 

		val left		  =cross<0 && !shoot
		val right     =cross>0 && !shoot

		val isNear    =dist<self.radius*minDistFactor

		val ahead	  = !isNear && !afraid

		isSucker      = abs(asin(selfDir.cross(targetDir))) < 3.1415927 * 0.8

		lastNextPublicId=next.publicId
		lastNextPos=next.pos
		lastCanShoot=canShoot
		lastSelfPos=self.pos

		action(left,right,ahead,shoot)
	}
}
