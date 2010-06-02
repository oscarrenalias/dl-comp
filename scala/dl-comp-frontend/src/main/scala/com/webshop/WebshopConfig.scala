package com.webshop

object WebshopConfig {	
  object URI {
    // URIs pointing to the different REST operations
    val BASE = "http://localhost:9998"
    val ITEM = "item"
    val CATALOG = "catalog"
    val ORDERS = "orders"
	val USER_ORDERS = ORDERS + "/user"
    val ORDER = "order"
  }
  
  val IMAGE_NOT_AVAILABLE = "http://server.com/imgs/image_not_available.png"
  val THUMBNAIL_NOT_AVAILABLE = "http://server.com/imgs/image_not_available.png"
}
