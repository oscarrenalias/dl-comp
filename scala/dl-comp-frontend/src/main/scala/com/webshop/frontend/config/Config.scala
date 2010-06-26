package com.webshop

object Config {

	/**
	 * This should come from a configuration file in real life
	 */	
	var data = List(
		"base_url" -> "http://ec2-67-202-14-141.compute-1.amazonaws.com:50000/demo.accenture.com~webshop~web/rest/",
		"item_uri" -> "item",
		"catalog_uri" -> "catalog",
		"orders_uri" -> "orders",
		"user_orders_uri" -> "orders/user",
		"order_uri" -> "order",
		"image_not_available_url" -> "http://server.com/imgs/image_not_available.png",
		"thumbnail_not_available_url" -> "http://server.com/imgs/image_not_available.png"
	)
	
	def get(key:String) = data.find(_._1.equals(key)) match {
		case Some((x,y)) => Some(y)
		case _ => None
	}
}

/**
 * Object that provides direct access to some of the key configuration properties
 * via (sort of) constants
 */
object WebshopConfig {	
	object URI {
		// URIs pointing to the different REST operations
		lazy val BASE = Config.get("base_url").get
		lazy val ITEM = Config.get("item_uri").get
		lazy val CATALOG = Config.get("catalog_uri").get
		lazy val ORDERS = Config.get("orders_uri").get
		lazy val USER_ORDERS = Config.get("user_orders_uri").get
		lazy val ORDER = Config.get("order_uri").get
	}
	
	lazy val IMAGE_NOT_AVAILABLE = Config.get("image_not_available_url").get
	lazy val THUMBNAIL_NOT_AVAILABLE = Config.get("thumbnail_not_available_url").get
}
