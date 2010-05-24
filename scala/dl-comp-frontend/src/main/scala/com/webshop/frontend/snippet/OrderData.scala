package com.webshop.frontend.snippet

import net.liftweb.http._ 
import scala.collection.mutable.ArrayBuffer
import _root_.net.liftweb.common.{Box,Full,Empty}
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
	  
	  val order = Order.create
	  
	  def submitOrder() = {
	    
		  Log.debug("Submitting order to backend with the following data:")
		  Log.debug(" ** Shopping cart:")
		  ShoppingCart.dumpCartData
		  Log.debug(" ** Order header data:")
		  Log.debug("address1: " + order.address1)
		  Log.debug("address2: " + order.address2)
    
		  order.setLineItems(ShoppingCart.getItems)
		  order.submit 
    
		  order.status match {
		    case OrderStatusValues.PROCESSED => {
		      // show a message
		      S.notice("Order " + order.number + " created successfully")
		      // clean the shopping cart
		      ShoppingCart.empty
        
		      checkoutOk = true
		    } 
		    case _ => { 
		      S.error({for(error <- order.getErrors) yield <li>error</li>}.mkString("<ul>","", "</ul>"))
		      checkoutOk = false
		     }
		  }
	  }
   
	  def validateAndSubmit(): Unit = {
	    order.validate match {
	      case Nil => submitOrder
	      case xs => S.error(xs);
	    }
	  }
   
	  bind("data", xhtml,
        "address1" -> order.address1.toForm,
        "address2" -> order.address2.toForm,
        "city" -> order.city.toForm,
        "postcode" -> order.postcode.toForm,
        "country" -> order.country.toForm,
        "phone" -> order.phone.toForm,
        "submit" -> SHtml.submit("Checkout", validateAndSubmit))
	}
}
