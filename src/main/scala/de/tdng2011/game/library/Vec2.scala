package de.tdng2011.game.library

import scala.math._

case class Vec2(x:Double,y:Double) {

	def +(v:Vec2)  = Vec2(x+v.x,y+v.y)
	def -(v:Vec2) = Vec2(x-v.x,y-v.y)
	def *(v:Vec2) = x*v.x + y*v.y
	def *(n:Double) = Vec2(x*n,y*n)
	def cross(v:Vec2) : Double = x*v.y - v.x*y   // only the z.component of the cross product
	def rotate(rad:Double) = Vec2( x*cos(rad) - y*sin(rad) , x*sin(rad) + y*cos(rad))

	def length = sqrt(x*x+y*y)

	override def toString = "("+x+","+y+")"
}