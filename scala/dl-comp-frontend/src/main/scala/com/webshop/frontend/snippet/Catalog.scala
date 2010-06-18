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
import com.webshop.frontend.comet._
import com.webshop.frontend.model.{Item => ModelItem,Catalog => ModelCatalog,CatalogCategory,RootCatalogCategory,User}
import com.webshop.frontend.snippet.bindings._
import com.webshop.frontend.snippet.bindings.Bindings._

object currentCategory extends RequestVar[Box[CatalogCategory]](Empty)

class Catalog {
	
	/**
	 * Provides a list of all the subcategories in the current category
	 */
	def list(xhtml:NodeSeq): NodeSeq = {	  
	  val category = currentCategory openOr RootCatalogCategory	  
	  implicit val catalogBinding = CatalogCategoryBinding
      category.getChildren.flatMap( row => row.bind(xhtml))
	}
	
	/**
	 * Similar to breadcrumbs, but this one must be placed in a block and allows to select
	 * which parts are shown
	 */
	def history(xhtml:NodeSeq): NodeSeq = {
		val category = currentCategory openOr RootCatalogCategory
		implicit val catalogBinding = CatalogCategoryBinding
		category.getParents.reverse.flatMap( row => row.bind(xhtml))
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

		implicit val catalogBinding = CatalogCategoryBinding		
	  	currentCategory.is match {
			case Full(x) => x.bind(xhtml)
			case _ => NodeSeq.Empty
	  	}
	}
 
	/**
	 * Provides a list of all the items available in the current category
	 */
	def items(xhtml:NodeSeq): NodeSeq = {	   
	  val category = currentCategory openOr RootCatalogCategory
	
	  ModelCatalog.getItems(category.id) match {
	    case None => {
	      Text("Items not found")
	      NodeSeq.Empty
	    }
        case Some(i) => {
			implicit val itemBinding = ItemBinding
			if(User.loggedIn_?) i.flatMap( row => row.bind(xhtml))
			else i.flatMap( row => row.bind(xhtml))
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
		implicit val catalogBinding = CatalogCategoryBinding
		if(cats.size > 0) cats.values.toList.sort({(a,b) => a.name < b.name}).flatMap(cat => cat.bind(xhtml))
		else Text("No subcategories found")		
	}
}