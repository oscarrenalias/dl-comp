package com.webshop.frontend.model

import scala.collection.mutable.ArrayBuffer
import net.liftweb.http._ 
import com.webshop.frontend.model.{Item=>ModelItem}
import com.webshop.frontend.model._
import _root_.net.liftweb.util.Log

object ShoppingCart extends SessionVar {
  
  type ShoppingCartLineItem = (Int, ModelItem)
  
  var items = new ArrayBuffer[ShoppingCartLineItem]  
  
  def addItem(amount: Int, item: ModelItem) = {
    items += (amount, item)
    dumpCartData
  }
  
  def removeItem(amount: Int, item: ModelItem) = {
    items -= (amount, item)
    dumpCartData
  }
  
  def getItems: ArrayBuffer[ShoppingCartLineItem] = items
  
  def getItemsForOrder: List[LineItemInfo] = items.flatMap({x=>List(LineItemInfo(x._2.id, x._1))}).toList
  
  def dumpCartData = {
    Log.debug("Items in cart " + items.size)
    items.foreach(i => Log.debug("item: " + i._2.id + " - amount:" + i._1.toString))
  }  
  
  def empty = items.clear
  
  def isEmpty = (items.length == 0)
  def hasItems = (items.length > 0)
}