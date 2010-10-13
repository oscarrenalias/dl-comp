package com.webshop

import net.lag.configgy.Configgy
// replaces net.liftweb.util.Log, deprecated as of Lift 2.0
import net.liftweb.common.Logger 

/**
 * Basic configuration object
 */
object Config extends Logger {
	
	lazy val config = {
		Configgy.configure("config/webshop.conf")		
		Configgy.config
	}
	
	lazy val mode =  config.getString("mode").getOrElse("")

	/**
	 * This should come from a configuration file in real life
	 */	
	var data = List(
		//"base_url" -> "http://ec2-67-202-14-141.compute-1.amazonaws.com:50000/demo.accenture.com~webshop~web/rest/",
		"base_url" -> "http://localhost:9998",
		"item_uri" -> "item",
		"catalog_uri" -> "catalog",
		"orders_uri" -> "orders",
		"user_orders_uri" -> "orders/user",
		"order_uri" -> "order",
		"image_not_available_url" -> "http://server.com/imgs/image_not_available.png",
		"thumbnail_not_available_url" -> "http://server.com/imgs/image_not_available.png"
	)
	
	/*def get(key:String) = data.find(_._1.equals(key)) match {
		case Some((x,y)) => Some(y)
		case _ => None
	}*/
	
	def get(key: String): Option[String] = config.getString(key) match {
		case None => warn("No configuration value found for key:" + key); get(key, mode)
		case Some(x) => Some(x)
	}
	
	def get(key: String, runMode: String): Option[String] = config.getString(runMode + "." + key) match {
		case None => warn("No configuration value found for key:" + key + " with mode: " + runMode); None
		case Some(x) => Some(x)
	}
}

/**
 * Object that provides direct access to some of the key configuration properties
 * via lazy variables
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
