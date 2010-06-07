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

object currentOrder extends RequestVar[Box[Order]](Empty)

class Orders {
  
	var checkoutOk = false
  
	def checkoutOk(xhtml: NodeSeq): NodeSeq = {
	  if (checkoutOk) xhtml else Text("") 
	}
 
 	def checkoutNotOk(xhtml: NodeSeq): NodeSeq = {
	  if (!checkoutOk) xhtml else Text("")
	}
  
	def add(xhtml: NodeSeq): NodeSeq = {
	  
	  val order = Order()
	  
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
        "address1" -> SHtml.text(order.address.address1, order.address.address1 = _),
        "address2" -> SHtml.text(order.address.address2, order.address.address2 = _),
        "city" -> SHtml.text(order.address.city, order.address.city = _),
        "postcode" -> SHtml.text(order.address.postcode, order.address.postcode = _),
        "country" -> SHtml.text(order.address.country, order.address.country = _),
        "phone" -> SHtml.text(order.contact.phone, order.contact.phone = _),
        "email" -> SHtml.text(order.contact.email, order.contact.email = _),
        "submit" -> SHtml.submit("Checkout", validateAndSubmit))  
	}
	
	/**
	 * Snippet that fetches the user's most recent orders
	 */
	def myOrders(xhtml: NodeSeq) = {
		Order.getUserOrders(User.currentUser.get.email) match {
			case Full(l) => {
				l.flatMap( order => { 
					var orderId = "order-info-" + order.id;
					bind("order", xhtml, 
					"info_link" -%> SHtml.a({() =>
						currentOrder(Full(order));
						SetHtml("order-info-" + order.id, <lift:embed what="/templates-hidden/order-data.html" />)},
						Text("Details")),
					"info_container" -> <div id={orderId}></div>,
					"id" -> order.id,						
					"status" -> order.status,
					"description" -> order.nicerDescription,
					"address1" -> order.address.address1,
					"address2" -> order.address.address2,
					"city" -> order.address.city,
					"postcode" -> order.address.postcode,
					"country" -> order.address.country,
					"email" -> order.contact.email,
					"phone" -> order.contact.phone
				)})
			}
			case _ => Text("No previous orders found")
		}
	}
	
	def order(xhtml: NodeSeq) = {
		currentOrder.is match {
			case Full(order) => {
				bind("order", xhtml, "id" -> order.id)
			}
			case _ => Text("No order found")
		}
	}
	
	def items(xhtml: NodeSeq) = {
		currentOrder.is match {
			case Full(order) => {
				order.items.flatMap(line => bind("item", xhtml, 
						"amount" -> line.amount,
						"description" -> line.description,
						"id" -> line.item ))
			}
			case _ => Text("No order found")
		}
	}
}
