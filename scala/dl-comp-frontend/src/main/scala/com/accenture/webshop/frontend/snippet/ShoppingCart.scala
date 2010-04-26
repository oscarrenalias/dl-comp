package com.accenture.webshop.frontend.snippet

import net.liftweb.http._ 
import scala.collection.mutable.ArrayBuffer
import com.accenture.webshop.frontend.rest.RestfulItem
import _root_.net.liftweb.util.{Box,Full,Empty,Helpers,Log}
import _root_.scala.xml.{NodeSeq,Text,Node,Elem}
import _root_.net.liftweb.util.Helpers._

object ShoppingCart extends SessionVar[ShoppingCart](new ShoppingCart) {
  
  def addItem(item: RestfulItem) = {
	  get.add(item)	 
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
    			"price" -> item.price.toString )
    	)
    }
  
  def toHtml: String = {
    {for(i <- ShoppingCart.items) yield <li>{i.id}: {i.desc}</li>}.mkString
  }  
}

class ShoppingCart {
  var items = new ArrayBuffer[RestfulItem]
  
  def add(item: RestfulItem) = {    
    items += item
    dumpCartData
  }
  
  def dumpCartData = {
    Log.debug("Items in cart " + items.size)
    items.foreach(i => Log.debug("item: " + i.id))
  } 
}