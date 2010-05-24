package com.webshop.frontend.model

import net.liftweb._ 
import mapper._ 
import http._ 
import SHtml._ 
import util._
import scala.collection.mutable.ArrayBuffer
import _root_.net.liftweb.common.{Box,Full,Empty}
import com.webshop.frontend.snippet._

class NonEmptyMappedString[T <: Mapper[T]](var owner: T, msg: String) extends MappedString(owner, 255) {
	 override def validations = 
		 valMinLen(1, msg) _ :: super.validations  
}
 
class Order extends LongKeyedMapper[Order] with IdPK { 
 def getSingleton = Order 
 
 object address1 extends NonEmptyMappedString(this, "Address 1 cannot be empty")
 object address2 extends NonEmptyMappedString(this, "Address 2 cannot be empty")
 object city extends NonEmptyMappedString(this, "City cannot be empty")
 object postcode extends NonEmptyMappedString(this, "Postcode cannot be empty")
 object country extends NonEmptyMappedString(this, "Country cannot be empty")
 object phone extends NonEmptyMappedString(this, "Phone cannot be empty")
 
 // order number, once it's been set to the ERP
 var number = ""
 
 // line items
 var items = new ArrayBuffer[ShoppingCart.ShoppingCartLineItem]
 
 // order status
 var status = OrderStatusValues.NEW
 
 def setLineItems(newItems: ArrayBuffer[ShoppingCart.ShoppingCartLineItem]) = {
   items = newItems 
 }
 
 def addLineItems(newItems: ArrayBuffer[ShoppingCart.ShoppingCartLineItem]) = {
   //items ++ newItems 
 }
 
 def submit: Unit = Order.submit(this)
 
 def getErrors: List[String] = List("Error number 1", "Error number 2", "Error number 3" )
}

/**
 * Constants with order status strings
 */
object OrderStatusValues {
  val NEW = "New"
  val PROCESSED = "Completed"
}

/**
 * Companion of the Order class
 */
object Order extends Order with LongKeyedMetaMapper[Order] {
  
  def submit(order: Order): Order = {
    order.status = OrderStatusValues.PROCESSED
    order.number = "123456789"
    
    order
  }
  
  def get(orderId: Int): Box[Order] = {
    None
  }
  
  def getAll(): Box[List[Order]] = {
    Empty
  }
}