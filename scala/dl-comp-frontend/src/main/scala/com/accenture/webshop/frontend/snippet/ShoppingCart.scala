package com.accenture.webshop.frontend.snippet

import net.liftweb.http._ 
import scala.collection.mutable.ArrayBuffer
import _root_.net.liftweb.util.{Box,Full,Empty,Helpers,Log}
import _root_.scala.xml.{NodeSeq,Text,Node,Elem}
import _root_.net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.jquery.JqJsCmds._
import com.accenture.webshop.frontend.rest.RestfulItem
import com.accenture.webshop.frontend.model._

object ShoppingCart extends SessionVar {
  
  var items = new ArrayBuffer[(Int, RestfulItem)]  
  
  def addItem(amount: Int, item: RestfulItem) = {
    items += (amount, item)
    dumpCartData
  }
  
  def removeItem(amount: Int, item: RestfulItem) = {
    items -= (amount, item)
    dumpCartData
  }
  
  def dumpCartData = {
    Log.debug("Items in cart " + items.size)
    items.foreach(i => Log.debug("item: " + i._2.id + " - amount:" + i._1.toString))
  }  
}

/**
 * Snippet for displaying shopping cart data
 */
class ShoppingCartData {
  def list(xhtml:NodeSeq): NodeSeq = {
    	ShoppingCart.items.flatMap( item =>
    	  bind( "item", xhtml, 
    			"id" -> item._2.id, 
    			"amount" -> item._1.toString,
    			"description" -> item._2.desc,       
    			"price" -> item._2.price.toString,
				"remove" -> SHtml.a({() =>
					ShoppingCart.removeItem(item._1, item._2)	
					SetHtml("shopping-cart", <lift:embed what="/templates-hidden/cart-data.html" />)}, Text("Remove"))
			)
    	)
  }  
}