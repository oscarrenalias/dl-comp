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
  
  var items = new ArrayBuffer[RestfulItem]  
  
  def addItem(item: RestfulItem) = {
    items += item
    dumpCartData
  }
  
  def removeItem(item: RestfulItem) = {
    items -= item
    dumpCartData
  }
  
  def dumpCartData = {
    Log.debug("Items in cart " + items.size)
    items.foreach(i => Log.debug("item: " + i.id))
  }  
}

/**
 * Snippet for displaying shopping cart data
 */
class ShoppingCartData {
  def list(xhtml:NodeSeq): NodeSeq = {

    
    	ShoppingCart.items.flatMap( item =>
    	  bind( "item", xhtml, 
    			"id" -> item.id, 
    			"amount" -> 1.toString,
    			"description" -> item.desc,       
    			"price" -> item.price.toString,
				"remove" -> SHtml.a({() =>
					ShoppingCart.removeItem(item)	
					SetHtml("shopping-cart", <lift:embed what="/templates-hidden/cart-data.html" />)}, Text("Remove"))
			)
    	)
    }
  
  def toHtml: String = {
    {for(i <- ShoppingCart.items) yield <li>{i.id}: {i.desc}</li>}.mkString
  }  
}