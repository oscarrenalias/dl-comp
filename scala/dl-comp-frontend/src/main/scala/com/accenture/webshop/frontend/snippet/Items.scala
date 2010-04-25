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
		RestfulItem.getAll.toList.flatMap( row =>
			bind( "item", xhtml, 
					"id" -> row._2.id, 
					"description" -> SHtml.link("/item", () => currentItem(Full(row._2)), Text(row._2.desc)),
					"price" -> row._2.price.toString )
		)
	}
}

class Item {
  def showInfo(xhtml: NodeSeq): NodeSeq = {
    def showItemData(item: RestfulItem): NodeSeq = {
      bind( "item", xhtml, 
			"id" -> item.id, 
			"description" -> item.desc, 
			"longDescription" -> item.longDescription,
			"price" -> item.price.toString,
      		"vendor" -> item.vendor )
    }
    
    currentItem.is match {
      case Full(item) => showItemData(item)
      case _ =>  { Text("Item not found")
                   NodeSeq.Empty }
    }
  }
}
