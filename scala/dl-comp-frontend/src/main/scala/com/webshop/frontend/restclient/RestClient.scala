package com.webshop.frontend.restclient

import java.net.URI
import com.sun.jersey.api.client._
import net.liftweb.json.JsonParser._
import _root_.net.liftweb.common.{Box,Full,Failure,Empty}
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonAST
import net.liftweb.json.JsonDSL._
import _root_.net.liftweb.util.Log
import com.webshop.frontend.model._
import com.webshop.{WebshopConfig => config}

trait RestfulOperator {
  
	implicit val formats = net.liftweb.json.DefaultFormats  
	var c = Client.create
	var r = new Resource(c, new URI(config.URI.BASE))
	
	def doGet(path: String) = {
		Log.debug("Executing GET - path = " + path)
		r.path(path).get(Request().acceptLanguageString("en"))
	}
	
	def doPut(path: String, data: String) = {
		Log.debug("Executing PUT - path = " + path)
		r.path(path).put(Request(data).acceptLanguageString("en"))
	}
 
	def doPost(path: String, data: String) = {
		Log.debug("Executing POST - path = " + path)
		r.path(path).post(Request(data).mediaType("application/json"))
	} 
}

object RestClient {

  object Items extends RestfulOperator {
	  def get(id: String): Box[Item] = {	    
        try {
          val response:String = doGet(config.URI.ITEM + "/" + id).getEntity(classOf[String])	    
	      Log.debug(config.URI.ITEM + "- item: " + id + " - response = " + response)  
       
          Full(parse(response).extract[Item])
          
        } catch {
          case e:Exception => Failure(e.getMessage, Full(e), Empty)                 
        }
      }
  }
  
  object Orders extends RestfulOperator {
    
    def get(id: String): Box[Order] = {
    	try {
    		var response:String = doGet(config.URI.ORDER + "/" + id).getEntity(classOf[String])
    		Log.debug("Order json = " + response)
    		Full(parse(response).extract[Order])
    	} catch {
    		case e:Exception => Failure(e.getMessage, Full(e), Empty)
    	}
    }
    
    def create(o: Order): Box[Order] = {
    	val jsonOrder = o.toJson 
    	Log.debug("serialized json Order: " + jsonOrder)
    	// post the order to the server
    	try {
    	  // call the service and extract the new order from the response
    	  var response:String = doPost(config.URI.ORDER + "/", jsonOrder).getEntity(classOf[String])
    	  Log.debug("Orders.create Response = " + response)
    	  Full(parse(response).extract[Order])
    	} catch {
    	  case e:Exception => Failure(e.getMessage, Full(e), Empty)
    	}
    }

	def getUserOrders(user: String): Box[OrderList] = {
    	try {
    		var response:String = doGet(config.URI.USER_ORDERS + "/" + user).getEntity(classOf[String])
    		Log.debug("Order json = " + response)
    		Full(parse(response).extract[OrderList])
    	} catch {
    		case e:Exception => Failure(e.getMessage, Full(e), Empty)
    	}		
	}
  }
}
