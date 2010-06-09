package com.webshop.frontend.snippet

import net.liftweb.http._ 
import _root_.net.liftweb.common.{Box,Full,Empty}
import _root_.net.liftweb.util.Log
import _root_.scala.xml.{NodeSeq,Text,Node,Elem}
import _root_.net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.jquery.JqJsCmds._
import com.webshop.frontend.model._
import com.webshop.frontend.model.{Item=>ModelItem}

/**
 * Snippet for displaying shopping cart data
 */
class ShoppingCartData {
		
  	def list(xhtml:NodeSeq): NodeSeq = {	
		ShoppingCart.items.flatMap( item =>
    	  bind( "item", xhtml, 
    			"id" -> item._2.id, 
    			"amount" -> item._1.toString,
    			"description" -> item._2.description,       
    			"price" -> Text(item._2.price + "€"),
				AttrBindParam("id", Text("FIXME"), "id"),
				"total_price" -> Text(((item._1 * item._2.price.toDouble).toString) + "€"),
				"remove" -> SHtml.a({() =>
					ShoppingCart.removeItem(item._1, item._2)
					SetHtml("shopping-cart-", <lift:embed what="/templates-hidden/summary-cart-data.html" />)}, Text(S.??("Remove")))
			)
		)
	}
	
	def info(xhtml:NodeSeq): NodeSeq = {
		if(User.loggedIn_?) {
			if(ShoppingCart.items.length == 0) {
				Text(S.??("Your shopping cart is empty"))
			}
			else {
				var itemsString = ""
				if(ShoppingCart.items.length == 1) itemsString = S.??("item")
				else itemsString = S.??("items")
				bind("cart", xhtml, 
					"number_of_items" -> Text(ShoppingCart.items.length.toString + " " + itemsString),
					"total_price" -> Text(ShoppingCart.totalPrice.toString  +  "€"))
			}
		}
		else {
			Text(S.??("Please log in to add items"))
		}
	}
}