package com.webshop.frontend.snippet.bindings

import scala.xml.{NodeSeq,Text,Node,Elem}  
import net.liftweb.common.{Box,Full,Empty,Failure}
import net.liftweb.http._
import net.liftweb.util.Helpers._
import com.webshop.frontend.model.{Item=>ModelItem,_}
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js._
import Bindings._

object ItemBinding extends DataBinding[ModelItem] {
	
    def addToCart(item:ModelItem): JsCmd = {
     		ShoppingCart.addItem(1, item)
			import net.liftweb.http.js.jquery.JqJsCmds.DisplayMessage 
			new DisplayMessage("messages", Text(S.?("Item added to shopping cart")), 5000, 1000)
    }	
	
	def apply(item: ModelItem): Binding = bind("item", _, 
		"id" -> item.id, 
		"name" -> item.name,
		"link" -%> SHtml.link("/item", () => currentItem(Full(item)), Text("Details")),
		"add_to_cart" -%> {(ns: NodeSeq) => 
			if(User.loggedIn_?) SHtml.a(() => addToCart(item), Text(S.?("Add to Cart")))
			else <span></span> },
		"description" -> item.description,
		"image" -%> <img src={item.getImage(0)} />,
		"thumbnail" -%> <img src={item.getThumbnail(0)} />,
		"currency" -> item.currency,
		"price" -> item.price.toString )
}

object CatalogCategoryBinding extends DataBinding[CatalogCategory] {
	def apply(cat: CatalogCategory): Binding = bind("category", _,
		"id" -> cat.id,
		"description" -> cat.description,
		"name" -> cat.name,
		"itemcount" -> cat.numProducts,
		"link" -%> SHtml.link("/browse", () => currentCategory(Full(cat)), Text(cat.name)))
}