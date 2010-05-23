package com.webshop.frontend.restclient

import java.net.URI
import com.sun.jersey.api.client._
import net.liftweb.json.JsonParser._
import _root_.net.liftweb.util.{Box,Full,Failure,Empty,Log} 
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
}

object RestClient {

  object Items extends RestfulOperator {
	  def get(id: String): Box[Item] = {
	    val response:String = doGet(config.URI.ITEM + "/" + id).getEntity(classOf[String])
	    
	    Log.debug(config.URI.ITEM + "- item: " + id + " - response = " + response)
	    
        try {          
          Full(parse(response).extract[Item])          
        } catch {
          case e:Exception => Failure(e.getMessage, Full(e), Empty)                 
        }
      }
  }
}
