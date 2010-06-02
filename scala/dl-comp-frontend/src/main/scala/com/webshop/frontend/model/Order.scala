package com.webshop.frontend.model

import net.liftweb._ 
import mapper._ 
import http._ 
import SHtml._ 
import util._
import scala.collection.mutable.ArrayBuffer
import net.liftweb.common.{Box,Full,Empty}
import com.webshop.frontend.snippet._
import com.webshop.frontend.restclient._

case class AddressInfo(var address1: String, var address2: String, var city: String, var country: String, var postcode: String)
case class ContactInfo(var name: String, var phone: String, var email: String)
case class LineItemInfo(var item: String, var amount: String) {
	def this(item:String, amount: Int) = this(item, amount.toString)
}

case class Order(var id: String, var description: String, var user: String, var status: String, var address: AddressInfo, var contact: ContactInfo, var items: List[LineItemInfo]) extends JsonSerializable { 
	// constructor without parameters
	def this() = {
		this("", "", "", "",
			 new AddressInfo("", "", "", "", ""), 
			 new ContactInfo("", "", ""),
			 List[LineItemInfo]())
	}
	
	def submit: Unit = Order.submit(this)
}

/**
 * Constants with order status strings
 */
object OrderStatusValues {
  val NEW = "New"
  val PROCESSED = "Completed"
  val CREATED = "Created"
}

/**
 * Companion of the Order class
 */
object Order {
  
  def submit(order: Order): Box[Order] = {    
    RestClient.Orders.create(order)
  }
  
  def get(orderId: String): Box[Order] = {
    RestClient.Orders.get(orderId)
  }
  
  def getAll(): Box[List[Order]] = {
    Empty
  }
}