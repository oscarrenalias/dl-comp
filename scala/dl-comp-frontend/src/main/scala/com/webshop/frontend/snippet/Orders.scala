package com.webshop.frontend.snippet

import net.liftweb.http._ 
import scala.collection.mutable.ArrayBuffer
import _root_.net.liftweb.common.{Box,Full,Empty,Failure}
import _root_.net.liftweb.util.Log
import _root_.scala.xml.{NodeSeq,Text,Node,Elem,UnprefixedAttribute,Null}
import _root_.net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.jquery.JqJsCmds._
import com.webshop.frontend.model._
import com.webshop.frontend.snippet.bindings._
import com.webshop.frontend.snippet.bindings.Bindings._
import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.common._
import Helpers._

object currentOrder extends RequestVar[Box[Order]](Empty)

/**
 * This class extends the LiftScreen objects so that we can generate a basic form
 * and provide field validation without using Lift's Mapper classes (because we
 * are not using Mapper objects anyway)
 */
object Checkout extends LiftScreen {
	
	// fields for the screen, including their validations and filters
	val name = field(S ? "Name", User.currentUser.get.firstName.is + " " + User.currentUser.get.lastName.is, 
					 trim, valMinLen(2, S ? "Name is too short"))					
	val address1 = field(S ? "Address 1", "", trim, valMinLen(5, S.?("Address is too short")))
	val address2 = field(S ? "Address 2", "", trim)
	val city = field(S ? "City", "", trim, valMinLen(2, S.?("A city must be provided")))
	val postcode = field(S ? "Postal code", "", trim, valMinLen(2, S.?("The postal code is not valid")))
	val country = field(S ? "Country", "", trim, valMinLen(2, S.?("The country is not valid")))
	val email = field(S ? "Email", User.currentUser.get.email.is, trim, valMinLen(2, S.?("Email address is not valid")))
	val phone = field(S ? "Phone number", "", trim, valMinLen(2, S.?("Phone is not valid")))
	
	// use a custom template for the LiftScreen 
	override def allTemplatePath = List("templates-hidden", "checkout")
	
	// custom checkout button
	override def finishButton: Elem = <button>{S.??("Checkout")}</button>
	
	/**
	 * Method overriden from LiftScreen to provide our own logic. This was required
	 * because in addition to the validations, we should also be able to stop
	 * the processign of the form after the validations, which is not possible with the
	 * default ipmlementation of doFinish
	 */
	override def doFinish() {
    	validate match {
      		case Nil => {
        		val snapshot = createSnapshot
				submitOrder match {
					case Full(o) => { 
						S.notice(S.?("Order created successfully with number ") + o.id)
						redirectBack()
					}
					case Failure(msg, x, y) => S.error(S.?("There was an error creating the order: ") + msg)
					case _ => S.error(S ? "There was an unknown error creating the order")
				}
			}
      		case xs => S.error(xs)
    	}
  	}	

	// must be implemented since this method is marked as abstract in the LiftScreen trait,
	// even though we don't really use it here
	override def finish() = Nil
	
	/**
	 * Private method that takes care of creating an Order object,
	 * submits it, and returns a Box with the results
	 *
	 * @see Order
	 */
	private def submitOrder = {
		// set the order header fields
		var order = Order()
		order.user = User.currentUser.get.email
		order.address.address1 = address1
		order.address.address2 = address2
		order.address.city = city
		order.address.postcode = postcode
		order.address.country = country
		order.contact.email = email
		order.contact.name = name
		order.contact.phone = phone
		
		// set the line items 
		order.items = ShoppingCart.getItemsForOrder		
		
		val result = order.submit
		result match {			
			case Full(x) => ShoppingCart.empty
			case _ => Nil
		}
		
		result
	}
	
	/**
	 * Reimplemented from LiftScreen so that instead of going back to the referrer page
	 * when the screen is over (default behaviour) we move the flow to another page
	 * of our choosing
	 */
	override def redirectBack() {
		S.seeOther("/index")
	}
}	

/**
 * Snippet that handles fetching and displaying orders
 */
class Orders {
  
	implicit val orderBinding = DefaultOrderBinding
	
	/**
	 * Snippet that fetches the user's most recent orders
	 */
	def myOrders(xhtml: NodeSeq) = {
		Order.getUserOrders(User.currentUser.get.email) match {
			case Full(l) => l.flatMap( order => order.bind(xhtml))
			case _ => Text("No previous orders found")
		}
	}
	
	def order(xhtml: NodeSeq) = {
		currentOrder.is match {
			case Full(order) => order.bind(xhtml)
			case _ => Text("No order found")
		}
	}
}
