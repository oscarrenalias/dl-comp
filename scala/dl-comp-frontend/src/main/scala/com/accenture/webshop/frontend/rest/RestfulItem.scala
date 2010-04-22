package com.accenture.webshop.frontend.rest

/**
 * Factory clas that implements the basic methods that allow to obtain
 * data from the restful interfaces
 */
object RestfulItem {

  	var a = new RestfulItem("4", "Item 4", 44)
	a.vendor = "Vendor"
	a.longDescription = "This is a long description"
  
	val items = List(new RestfulItem("1", "Item 1", 11), 
			new RestfulItem("2", "Item 2", 22), 
			new RestfulItem("3", "Item 3", 33), 
			a)
  
	/**
	 * To be fixed later. currently only returning static data 
	 */
	def getAll: List[RestfulItem] = {
		items
	}
}

/**
 * Base class that represents an item obtained from a restful call 
 * It's a simple bean for now
 */
class RestfulItem (var id: String, var desc: String, var price: Integer ) {
	 var vendor = ""
	 var longDescription = ""
}