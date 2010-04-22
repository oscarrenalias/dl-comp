package com.accenture.webshop.frontend.snippet
import _root_.scala.xml.{NodeSeq,Text,Node,Elem}  
import _root_.net.liftweb.util.{Box,Full,Empty,Helpers,Log}  
import _root_.net.liftweb.util.Helpers._
import net.liftweb.http._

import com.accenture.webshop.frontend.logging._
import com.accenture.webshop.frontend.model._
import com.accenture.webshop.frontend.rest._

object currentItem extends RequestVar[Box[RestfulItem]](Empty)

class Items {
	def list(xhtml:NodeSeq): NodeSeq = {	  
		RestfulItem.getAll.flatMap( row =>
			bind( "item", xhtml, 
					"id" -> row.id, 
					"description" -> SHtml.link("/item", () => currentItem(Full(row)), Text(row.desc)),
					"price" -> row.price.toString )
		)
	}
}
