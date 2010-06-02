package com.webshop.frontend.model

import net.liftweb._ 
import mapper._ 
import http._ 
import SHtml._ 
import util._
import net.liftweb.common.{Box,Full,Empty,Failure}
import _root_.scala.xml.{NodeSeq,Text,Node,Elem}
import com.webshop.frontend.snippet._
import com.webshop.frontend.restclient._

case class AddressInfo(var address1: String, var address2: String, var city: String, var country: String, var postcode: String)
case class ContactInfo(var name: String, var phone: String, var email: String)
case class LineItemInfo(var item: String, var amount: String) {
	def this(item:String, amount: Int) = this(item, amount.toString)
}

case class Order(var id: String, var description: String, var user: String, var status: String, var address: AddressInfo, var contact: ContactInfo, var items: List[LineItemInfo]) extends JsonSerializable { 
	// constructor without parameters
	/*def this() = {
		this("", "", "", "",
			 new AddressInfo("", "", "", "", ""), 
			 new ContactInfo("", "", ""),
			 List[LineItemInfo]())
	}*/
	
	def submit: Box[Order] = {
		val result = Order.submit(this)
		result match {
    		case Full(x) => {
    			Log.debug(x.toString)
    			id = x.id
    			status = x.status    			
    		}
    		case Failure(msg,x,y) => status = OrderStatusValues.ERROR
		}
		// we return the new order so that Failure messages are passed up to the UI
		result
	}
	
	/**
	 * Check if the order can be submitted. We are not using Lift's Mapper 
	 * classes so we need to do the validation manually
	 * @return
	 */
	def validate: Option[List[NodeSeq]] = {
		var errors = List[NodeSeq]()
		if( address.address1 == "" ) errors + Text("Address cannot be empty")
		if( address.city == "" ) errors + Text("City cannot be empty")
		if( address.country == "" ) errors + Text("Country cannot be empty")
		if( address.postcode == "" ) errors + Text("Postcode cannot be empty")
		if( contact.name == "" ) errors + Text("Name cannot be empty")
		if( contact.phone == "" ) errors + Text("Phone cannot be empty")
		if( contact.email == "" ) errors + Text("Email cannot be empty")		
		
		if (errors.length == 0) None else Some(errors)
	}
}

/**
 * Constants with order status strings
 */
object OrderStatusValues {
  val NEW = "New"
  val PROCESSED = "Completed"
  val CREATED = "Created"
  val ERROR = "Error"
}

/**
 * Companion of the Order class
 */
object Order {
		
	def apply() = {
		new Order("", "", "", "",
			 new AddressInfo("", "", "", "", ""), 
			 new ContactInfo("", "", ""),
			 List[LineItemInfo]())
	}	
  
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