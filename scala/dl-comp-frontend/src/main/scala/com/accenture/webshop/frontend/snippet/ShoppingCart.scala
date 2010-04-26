package com.accenture.webshop.frontend.snippet

import net.liftweb.http._ 
import scala.collection.mutable.HashSet
import com.accenture.webshop.frontend.rest.RestfulItem
import _root_.net.liftweb.util.{Box,Full,Empty,Helpers,Log}

object ShoppingCart extends SessionVar[ShoppingCart](new ShoppingCart) {
  
  def addItem(item: RestfulItem) = {
	  get.add(item)
  }  
}

class ShoppingCart {
  var items = new HashSet[RestfulItem]
  
  def add(item: RestfulItem) = {    
    items += item
    dumpCartData
  }
  
  def dumpCartData = {
    Log.debug("Items in cart " + items.size)
    items.foreach(i => Log.debug("item: " + i.id))
  }
}