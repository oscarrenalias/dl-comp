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

class OrderData {
  
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
}
