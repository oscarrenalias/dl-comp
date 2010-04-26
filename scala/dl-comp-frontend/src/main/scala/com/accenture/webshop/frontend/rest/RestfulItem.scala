package com.accenture.webshop.frontend.rest

import scala.collection.mutable.HashMap

/**
 * Factory clas that implements the basic methods that allow to obtain
 * data from the restful interfaces
 */
object RestfulItem {
  
	type ItemDataType = HashMap[String,RestfulItem]
  
	// static test data
	val items = new ItemDataType {
	  put("1", new RestfulItem("1", "Item 1", 11))
	  put("2", new RestfulItem("2", "Item 2", 22))
	  put("3", new RestfulItem("3", "Item 3", 33))
  	  put("4", new RestfulItem("4", "Item 4", 44) {
  	    vendor = "Vendor"
        longDescription = "This is a long description"
  	  })	  
	} 
 
	/**
	 * To be fixed later. currently only returning static data 
	 */
	def getAll: ItemDataType = {
		items
	}
 
	def get(id: String): RestfulItem = items.get(id).get
}

/**
 * Base class that represents an item obtained from a restful call 
 * It's a simple bean for now
 */
class RestfulItem (var id: String, var desc: String, var price: Integer ) {
	 var vendor = ""
	 var longDescription = ""
}