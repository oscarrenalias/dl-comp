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
import com.webshop.frontend.model.{Item => ModelItem,Catalog => ModelCatalog,CatalogCategory,RootCatalogCategory}
import com.webshop.frontend.snippet.bindings._
import com.webshop.frontend.snippet.bindings.Bindings._

object currentItem extends RequestVar[Box[ModelItem]](Empty)

class Item {
  
  def info(xhtml: NodeSeq): NodeSeq = {        
	
    def showItemData(item: ModelItem): NodeSeq = {
		implicit val itemBinding = ItemBinding	
		SHtml.ajaxForm(item.bind(xhtml) ++ SHtml.hidden(() => ItemBinding.addToCartForm(item)))
	}
    
    currentItem.is match {
      case Full(item) => showItemData(item)
      case _ =>  Text("Item not found")
    }
  }
}