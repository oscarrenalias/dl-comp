package com.webshop.frontend.snippet

import net.liftweb.http._ 
import scala.collection.mutable.ArrayBuffer
import _root_.net.liftweb.util.{Box,Full,Empty,Helpers,Log}
import _root_.scala.xml.{NodeSeq,Text,Node,Elem}
import _root_.net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.jquery.JqJsCmds._
import com.webshop.frontend.model._

object ShoppingCart extends SessionVar {
  
  type ShoppingCartLineItem = (Int, Item)
  
  var items = new ArrayBuffer[ShoppingCartLineItem]  
  
  def addItem(amount: Int, item: Item) = {
    items += (amount, item)
    dumpCartData
  }
  
  def removeItem(amount: Int, item: Item) = {
    items -= (amount, item)
    dumpCartData
  }
  
  def getItems: ArrayBuffer[ShoppingCartLineItem] = items
  
  def dumpCartData = {
    Log.debug("Items in cart " + items.size)
    items.foreach(i => Log.debug("item: " + i._2.id + " - amount:" + i._1.toString))
  }  
  
  def empty = items.clear
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
    			"description" -> item._2.description,       
    			"price" -> item._2.price,
				"remove" -> SHtml.a({() =>
					ShoppingCart.removeItem(item._1, item._2)	
					SetHtml("shopping-cart", <lift:embed what="/templates-hidden/cart-data.html" />)}, Text("Remove"))
			)
    	)
  }  
}