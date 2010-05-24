package com.webshop.frontend.snippet
import _root_.scala.xml.{NodeSeq,Text,Node,Elem}  
import _root_.net.liftweb.common.{Box,Full,Empty}  
import _root_.net.liftweb.util.Log
import _root_.net.liftweb.util.Helpers._
import net.liftweb.http._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.jquery.JqJsCmds._ 
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js._

import com.webshop.frontend.logging._
import com.webshop.frontend.model.{Item => ModelItem,Catalog => ModelCatalog,CatalogCategory,RootCatalogCategory}

object currentItem extends RequestVar[Box[ModelItem]](Empty)
object currentCategory extends RequestVar[Box[CatalogCategory]](Empty)

class Catalog {
	def list(xhtml:NodeSeq): NodeSeq = {	  
	  val category = currentCategory openOr RootCatalogCategory
      category.getChildren.flatMap( row =>
      bind( "category", xhtml, 
            "id" -> row.id,
            "description" -> row.description, 
            "name" -> SHtml.link("/browse", () => currentCategory(Full(row)), Text(row.name)))
      )  
	}
 
	def category(xhtml:NodeSeq): NodeSeq = {
	  val category = currentCategory openOr RootCatalogCategory
	  bind("category", xhtml,
		   "id" -> category.id,
		   "description" -> category.description,
		   "name" -> category.name,
		   "link" -> SHtml.link("/browse", () => currentCategory(Full(category)), Text(category.name)),
		   "breadcrumbs" -> Text("bread -> crumbs"))
	}
 
	def items(xhtml:NodeSeq): NodeSeq = {	   
	  val category = currentCategory openOr RootCatalogCategory
	  ModelCatalog.getItems(category.id) match {
	    case None => {
	      Text("Items not found")
	      NodeSeq.Empty
	    }
        case Some(i) => {
        	i.flatMap( row =>
			  bind( "item", xhtml, 
					"id" -> row.id, 
					"name" -> SHtml.link("/item", () => currentItem(Full(row)), Text(row.name)),
					"price" -> row.price.toString )
			)          
        }
      }
	}
}

class ItemInfo {
  
  def show(xhtml: NodeSeq): NodeSeq = {        
    
    var amount = "1";    
    
    def showItemData(item: ModelItem): NodeSeq = {      
            
      def addToCart(): JsCmd = {
          ShoppingCart.addItem(amount.toInt, item)          
          JqSetHtml("shopping-cart", <lift:embed what="/templates-hidden/cart-data.html" />)          
      }
      
      SHtml.ajaxForm(bind( "item", xhtml, 
			"id" -> item.id, 
			"name" -> item.name, 
			"description" -> item.description,
			"price" -> item.price,
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