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

/**
 * These are the different case classes that are used to encapsulate messages
 * that are passed to the cart actor manager and the cart comet actors
 */
case class AddListener(listener:CometActor)
case class RemoveListener(listener:CometActor)
case class CartUpdated
case class CartItemAdded(item:Item)
case class CartItemRemoved(item:Item)
case class CartItemUpdated(item:Item)
case class Success(success:Boolean)

/**
 * This class takes care of keeping track all the different comet
 * actors that may be present in a page, and ensures that messages
 * are forwarded between them accordingly
 */
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

/**
 * Implements the dynamic comet-based shopping cart snippet that dispays the
 * contents of the shopping cart in a page.
 *
 * Classes that perform operations on the shopping cart need not worry about
 * notifying the shopping cart, as it is all taken care internally by
 *  the ShoppingCart class
 */
class CartActor extends CometActor {
	
	override def defaultPrefix = Full("cart")
	
	override def devMode = true

	/**
	 * Renders the contents of the shopping cart, or a message if the shopping
	 * cart is empty
	 */
	def render = {
		if(User.loggedIn_?)
			<lift:embed what="/templates-hidden/summary-cart-data"/>
		else
			<span>Please log in to add items to your shopping cart</span>
	}
	
	/**
	 * Logic related to setting up the actor
	 */
	override def localSetup = {
		Log.debug("Shopping cart actor started")

        CartActorManager !? AddListener(this) match {
            case Success(true) => Log.debug("Listener added")
            case _ => Log.error("There was an issue registering the listener")
        }
	}
	
	/**
	 * Removes itself from the listener manager 
	 */
	override def localShutdown = {
		Log.debug("Shopping cart actor shutting down")
		CartActorManager ! RemoveListener(this)
	}
	
	/**
	 * Processes messages 
	 */
	override def lowPriority = {
		case CartUpdated => reRender(true)
		case CartItemAdded(i) => reRender(true)
		case CartItemRemoved(i) => reRender(true)		
		case CartItemUpdated(i) => reRender(true)		
		case _ => Log.error("The listener did not understand the message")
	}	
}