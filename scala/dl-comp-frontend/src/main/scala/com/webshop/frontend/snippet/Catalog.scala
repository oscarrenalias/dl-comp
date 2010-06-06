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
import com.webshop.frontend.model.ShoppingCart
import com.webshop.frontend.model.{Item => ModelItem,Catalog => ModelCatalog,CatalogCategory,RootCatalogCategory,User}

object currentCategory extends RequestVar[Box[CatalogCategory]](Empty)

class Catalog {
	
	/**
	 * Provides a list of all the subcategories in the current category
	 */
	def list(xhtml:NodeSeq): NodeSeq = {	  
	  val category = currentCategory openOr RootCatalogCategory	  
      category.getChildren.flatMap( row =>
      bind( "category", xhtml, 
            "id" -> row.id,
            "description" -> row.description, 
            "name" -> SHtml.link("/browse", () => currentCategory(Full(row)), Text(row.name)))
      )  
	}
	
	/**
	 * Similar to breadcrumbs, but this one must be placed in a block and allows to select
	 * which parts are shown
	 */
	def history(xhtml:NodeSeq): NodeSeq = {
		val category = currentCategory openOr RootCatalogCategory
		category.getParents.reverse.flatMap( row => 
			bind( "category", xhtml,
				  "id" -> row.id,
				  "description" -> row.description,
				  "name" -> SHtml.link("/browse", () => currentCategory(Full(row)), Text(row.name)))
		)
	}
	
	/**
	 * Provides a link to the root of the catalog
	 */
	def root: NodeSeq = {
		SHtml.link("/browse", () => currentCategory(Empty), Text(RootCatalogCategory.name))
	}
	
	/**
	 * Easily generates a nice "bread crubms" navigator
	 */
	def breadcrumbs: NodeSeq = {
		def showBreadCrumbs(c:CatalogCategory) = {
			c.getParents.reverse.flatMap(
				{ x => List(SHtml.link("/browse", () => currentCategory(Full(x)), Text(x.name))) ::: List(Text(" / ")) }
			)			
		}
		
		currentCategory.is match {
			case Full(x) => showBreadCrumbs(x)
			case _ => NodeSeq.Empty
		}		
	}
 
  	/**
 	 * Allows to provide information about the currently selected category
  	 */
	def category(xhtml:NodeSeq): NodeSeq = {

	  def showCurrentCategory(c:CatalogCategory) = {
		  bind("category", xhtml,
			   "id" -> c.id,
			   "description" -> c.description,
			   "name" -> c.name,		
			   "link" -> SHtml.link("/browse", () => currentCategory(Full(c)), Text(c.name)))		
	  }
		
	  currentCategory.is match {
		case Full(x) => showCurrentCategory(x)
		case _ => NodeSeq.Empty
	  }
	}
 
	/**
	 * Provides a list of all the items available in the current category
	 */
	def items(xhtml:NodeSeq): NodeSeq = {	   
	  val category = currentCategory openOr RootCatalogCategory
	
      def addToCart(item:ModelItem): JsCmd = {
       		ShoppingCart.addItem(1, item)          
       		JqSetHtml("shopping-cart", <lift:embed what="/templates-hidden/cart-data.html" />)
      }	
	
	  ModelCatalog.getItems(category.id) match {
	    case None => {
	      Text("Items not found")
	      NodeSeq.Empty
	    }
        case Some(i) => {
			// TODO: this can probably be optimized a bit
			if(User.loggedIn_?) i.flatMap( row =>
			  bind( "item", xhtml, 
					"id" -> row.id, 
					"name" -> row.name,
					"link" -%> SHtml.link("/item", () => currentItem(Full(row)), Text("Details")),
					"add_to_cart" -%> SHtml.a(() => addToCart(row), Text("Add to Cart")),
					"description" -> row.description,
					"image" -%> <img src={row.getImage(0)} />,
					"thumbnail" -%> <img src={row.getThumbnail(0)} />,
					"currency" -> row.currency,
					"price" -> row.price.toString ))
			else i.flatMap( row =>
			  bind( "item", xhtml, 
					"id" -> row.id, 
					"name" -> row.name,
					"link" -%> SHtml.link("/item", () => currentItem(Full(row)), Text("Details")),
					"add_to_cart" -> <span></span>,
					"description" -> row.description,
					"image" -%> <img src={row.getImage(0)} />,
					"thumbnail" -%> <img src={row.getThumbnail(0)} />,
					"currency" -> row.currency,
					"price" -> row.price.toString ))			
        }
      }
	}
	
	def categories(xhtml: NodeSeq): NodeSeq = {
		var catId = S.attr("level") openOr "0"
		catId match {
			case "current" => {
				val category = currentCategory openOr RootCatalogCategory
				catId = category.id.toString
			}
		}

		val cats = ModelCatalog.getCategories(catId.toInt)
		if(cats.size > 0) cats.values.toList.sort({(a,b) => a.name < b.name}).flatMap(cat => 
			bind("category", xhtml,
			"id" -> cat.id,
			"description" -> cat.description,
			"name" -> cat.name,
			"itemcount" -> cat.numProducts,
			"link" -%> SHtml.link("/browse", () => currentCategory(Full(cat)), Text(cat.name))))
		else Text("No subcategories found")
	}
}