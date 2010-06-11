package com.webshop.frontend.comet

import scala.actors.Actor
import scala.actors.Actor._
import net.liftweb.http.CometActor 
import net.liftweb.http.S
import _root_.net.liftweb.util.Log
import _root_.net.liftweb.util.Helpers._
import _root_.scala.xml.{NodeSeq,Text,Node,Elem}
import _root_.net.liftweb.common.{Box,Full,Empty}
import com.webshop.frontend.model._
import scala.actors.Actor._
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer

case class AddListener(listener:CometActor)
case class RemoveListener(listener:CometActor)
case class CartUpdated
case class CartItemAdded(item:Item)
case class CartItemRemoved(item:Item)
case class CartItemUpdated(item:Item)
case class Success(success:Boolean)

object CartActorManager extends Actor {
	val listeners = new ListBuffer[CometActor]
	
	def act = {
		loop { 
			react {
				// the add and remove listener messages are for the manager,
				// but everything else must be passed to the listeners
				case AddListener(listener) => {
						listeners += listener 
						reply(Success(true))
				}
				case RemoveListener(listener) => {
					listeners -= listener
				}
				case msg => listeners.foreach((actor) => {
						actor ! msg
				})
			}
		}
	}
	
	Log.info("Starting CartActorManager")
	start
}

class CartActor extends CometActor {
	
	override def defaultPrefix = Full("cart")
	
	override def devMode = true
	
	def render = {
		if(User.loggedIn_?)
			<lift:embed what="templates-hidden/summary-cart-data.html"/>
		else
			<span>Please log in to add items to your shopping cart</span>
	}
	
	override def localSetup = {
		Log.debug("Shopping cart actor started")

        CartActorManager !? AddListener(this) match {
            case Success(true) => Log.debug("Listener added")
            case _ => Log.error("There was an issue registering the listener")
        }
	}
	
	override def localShutdown = {
		Log.debug("Shopping cart actor shutting down")
		CartActorManager ! RemoveListener(this)
	}
	
	override def lowPriority = {
		case CartUpdated => {
			Log.debug("Cart has just been updated")
			reRender(true)
		}
		case CartItemAdded(i) => {
			Log.debug("Added one item")
			reRender(true)
		}
		case CartItemRemoved(i) => {
			Log.debug("Removed one item")
			reRender(true)
		}		
		case CartItemUpdated(i) => {
			Log.debug("Updated one item")
			reRender(true)
		}		
		case _ => {
			Log.error("The listener did not understand the message")
		}
	}	
}