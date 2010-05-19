package com.webshop.frontend.snippet
import _root_.scala.xml.{NodeSeq,Text,Node,Elem}  
import _root_.net.liftweb.util.{Box,Full,Empty,Helpers,Log}  
import _root_.net.liftweb.util.Helpers._
import net.liftweb.http._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.jquery.JqJsCmds._ 
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js._

import com.webshop.frontend.logging._
import com.webshop.frontend.model._
import com.webshop.frontend.rest._

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
    
    var amount = "1";    
    
    def showItemData(item: RestfulItem): NodeSeq = {      
            
      def addToCart(): JsCmd = {
          ShoppingCart.addItem(amount.toInt, item)          
          JqSetHtml("shopping-cart", <lift:embed what="/templates-hidden/cart-data.html" />)          
      }
      
      SHtml.ajaxForm(bind( "item", xhtml, 
			"id" -> item.id, 
			"description" -> item.desc, 
			"longDescription" -> item.longDescription,
			"price" -> item.price.toString,
      		"vendor" -> item.vendor,
      		"amountToCart" -> SHtml.text(amount, amount = _),
      		"addToCart" -> SHtml.submit("Add to cart", addToCart )
        ) ++ SHtml.hidden(addToCart))
    }       
    
    currentItem.is match {
      case Full(item) => showItemData(item)
      case _ =>  { Text("Item not found")
                   NodeSeq.Empty }
    }
  }
}