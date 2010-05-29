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
		SHtml.link("/browse", () => currentCategory(Empty), Text("Start"))
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
	  ModelCatalog.getItems(category.id) match {
	    case None => {
	      Text("Items not found")
	      NodeSeq.Empty
	    }
        case Some(i) => {
          /** 
              TODO: this is the same information that ItemInfo.show is showing - how can we have the same
              code for both? Recursive/embedded snippet? 
           **/
        	i.flatMap( row =>
			  bind( "item", xhtml, 
					"id" -> row.id, 
					"name" -> SHtml.link("/item", () => currentItem(Full(row)), Text(row.name)),
					"description" -> row.description,
					"image" -> <img src={row.getImage(0)} alt="Image" />,
					"thumbnail" -> <img src={row.getThumbnail(0)} alt="Thumbnail" />,		
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
			"image" -> item.getImage(0),
			"thumbnail" -> item.getThumbnail(0),
      		"addToCart" -> SHtml.submit("Add to cart", addToCart )
        ) ++ SHtml.hidden(addToCart))
    }    
    
    currentItem.is match {
      case Full(item) => showItemData(item)
      case _ =>  { Text("Item not found")
                   NodeSeq.Empty }
    }
  }
  
  def images(xhtml:NodeSeq): NodeSeq = {
	 currentItem.is match {
	   case Full(item) => {
	     item.getImages.flatMap( image =>
	       bind("image", xhtml, "url" -> <img class="item-image" src={image} alt="Image" /> )
         )
	   }
	   case _ => NodeSeq.Empty 
	 }		  
  }
  
  def thumbnails(xhtml:NodeSeq): NodeSeq = {
    val mode = S.attr("mode") openOr "default"
	 
    currentItem.is match {
	   case Full(item) => {
	     if( mode == "withLinks" ) {
	    	 item.images.flatMap( image =>
	    	 bind("thumbnail", xhtml, "url" -> <a href={image.large} class="image-thumbnail-link"><img class="item-thumbnail" data-large={image.large} src={image.small} alt="Image" /></a> )
	    	 )       
	     }
	     else {
	    	 item.images.flatMap( image =>
	    	 bind("thumbnail", xhtml, "url" -> <img class="item-thumbnail" data-large={image.large} src={image.small} alt="Image" /> )
	    	 )	       
	     } 	     
	   }
	   case _ => NodeSeq.Empty 
	 }
  }
}