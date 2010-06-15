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
import com.webshop.frontend.model.Item

case class AddressInfo(var address1: String, var address2: String, var city: String, var country: String, var postcode: String)
case class ContactInfo(var name: String, var phone: String, var email: String)
case class LineItemInfo(var item: String, var amount: String) {
	def getItem = Item.get(item)
	
	lazy val description = getItem.get.description
	
	lazy val boxedItem:Box[Item] = getItem
}
object LineItemInfo {
	def apply(item:String, amount: Int) = new LineItemInfo(item, amount.toString)	
}

case class Order(var id: String, var description: String, var user: String, var status: String, var address: AddressInfo, var contact: ContactInfo, var items: List[LineItemInfo]) extends JsonSerializable { 	

	lazy val nicerDescription = (if(description.equals("")) "No description" else description)
	
	def submit: Box[Order] = {
		val result = Order.submit(this)
		result match {
    		case Full(x) => {
    			Log.debug(x.toString)
    			id = x.id
    			status = x.status    			
    		}
    		case Failure(msg,x,y) => status = Order.Status.ERROR
			case _ => Order.Status.ERROR
		}
		// we return the new order so that Failure messages are passed up to the UI
		result
	}

	def getTotalPrice = {
		var total = 0.0
		for(item <- items) {
			val price = item.boxedItem match {
				case Full(x) => x.price.toDouble
				case _ => 0.0
			}
			total += price * item.amount.toDouble			
		}
			
		total
	}
	
	def getTotalItems:Int = items.foldLeft(0)((total, item) => item.amount.toInt + total)
	
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
 * Companion of the Order class
 */
object Order {
	
	object Status {
	  val NEW = "New"
	  val PROCESSED = "Completed"
	  val CREATED = "Created"
	  val ERROR = "Error"		
	}
		
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
  
  def getUserOrders(userId: String): Box[List[Order]] = {
	  // instead of passing the OrderList response directly, we will 
	  // extract the List itself and return it as such, which is nicer to work with
	  var userOrders = RestClient.Orders.getUserOrders(userId)
	  if(userOrders.isDefined) Full(userOrders.get.orders) else Failure(userOrders.toString)
  }
}

/**
 * Case class for deserializing responses containing a list of orders
 */
case class OrderList(var orders: List[Order])