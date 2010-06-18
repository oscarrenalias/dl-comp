package com.webshop.frontend.snippet

import net.liftweb.http._ 
import scala.collection.mutable.ArrayBuffer
import _root_.net.liftweb.common.{Box,Full,Empty,Failure}
import _root_.net.liftweb.util.Log
import _root_.scala.xml.{NodeSeq,Text,Node,Elem}
import _root_.net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.jquery.JqJsCmds._
import com.webshop.frontend.model._
import com.webshop.frontend.snippet.bindings._
import com.webshop.frontend.snippet.bindings.Bindings._

object currentOrder extends RequestVar[Box[Order]](Empty)

class Orders {
  
	var checkoutOk = false
	
	implicit val orderBinding = DefaultOrderBinding
  
	def checkoutOk(xhtml: NodeSeq): NodeSeq = {
	  if (checkoutOk) xhtml else Text("") 
	}
 
 	def checkoutNotOk(xhtml: NodeSeq): NodeSeq = {
	  if (!checkoutOk) xhtml else Text("")
	}
  
	def add(xhtml: NodeSeq): NodeSeq = {
	  
	  val order = Order()
	  
	  val textFieldSize = "70"
	  
	  def submitOrder() = {
    
		  order.items = ShoppingCart.getItemsForOrder
		
		  // set the current user
		  if (User.loggedIn_?) order.user = User.currentUser.get.email; else "nouser"
    
		  order.submit match {
		 	  case Full(o) => {
		 		  // clean the shopping cart
		 		  ShoppingCart.empty
		 		  checkoutOk = true		 	 	  
		 		  // show a message
		 	 	  S.redirectTo("/browse", () => S.notice("Order " + o.id + " created successfully"))
		 	  }
		 	  case Failure(msg, x, y) => {
		 		  //S.error({for(error <- order.getErrors) yield <li>error</li>}.mkString("<ul>","", "</ul>"))
		 		  S.error(msg)
		 		  checkoutOk = false		 	 	  
		 	  }
		 	  case _ => {
		 	 	  S.error("Unkown error")
		 	 	  checkoutOk = false
		 	  }
		  }
	  }
   
	  def validateAndSubmit(): Unit = {
	    order.validate match {
	      case None => submitOrder
	      case Some(errors) => errors.foreach(S.error(_))
	      case _ => S.error("Unknown validation error")
	    }
	  }
        
      bind("data", xhtml,
        "address1" -> SHtml.text(order.address.address1, order.address.address1 = _) % ("size" -> textFieldSize),
        "address2" -> SHtml.text(order.address.address2, order.address.address2 = _) % ("size" -> textFieldSize),
        "city" -> SHtml.text(order.address.city, order.address.city = _) % ("size" -> textFieldSize),
        "postcode" -> SHtml.text(order.address.postcode, order.address.postcode = _) % ("size" -> textFieldSize),
        "country" -> SHtml.text(order.address.country, order.address.country = _) % ("size" -> textFieldSize),
        "phone" -> SHtml.text(order.contact.phone, order.contact.phone = _) % ("size" -> textFieldSize),
        "email" -> SHtml.text(order.contact.email, order.contact.email = _) % ("size" -> textFieldSize),
        "submit" -> SHtml.submit("Checkout", validateAndSubmit))  
	}
	
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
			case Full(order) => {
				implicit val orderBinding = DefaultOrderBinding
				order.bind(xhtml)
			}
			case _ => Text("No order found")
		}
	}
}
