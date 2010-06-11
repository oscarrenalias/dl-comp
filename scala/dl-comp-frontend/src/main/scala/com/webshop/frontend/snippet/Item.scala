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
import com.webshop.frontend.model.{Item => ModelItem,Catalog => ModelCatalog,CatalogCategory,RootCatalogCategory}

object currentItem extends RequestVar[Box[ModelItem]](Empty)

class Item {
  
  def info(xhtml: NodeSeq): NodeSeq = {        
    
    var amount = "1";    
    
    def showItemData(item: ModelItem): NodeSeq = {      
            
      def addToCart() = {
		  import bootstrap.liftweb.MenuInfo	
          ShoppingCart.addItem(amount.toInt, item)
		  MenuInfo.setSitemap
      }
      
      SHtml.ajaxForm(bind( "item", xhtml, 
			"id" -> item.id, 
			"name" -> item.name, 
			"description" -> item.description,
			"price" -> item.price,
      		"amountToCart" -%> SHtml.text(amount, amount = _),
			"image" -> item.getImage(0),
			"thumbnail" -> item.getThumbnail(0),
      		"addToCart" -%> SHtml.submit("Add to cart", addToCart ),
			"currency" -> item.currency
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
	    	 bind("thumbnail", xhtml, "url" -%> <img class="item-thumbnail" data-large={image.large} src={image.small} /> )
	    	 )	       
	     } 	     
	   }
	   case _ => NodeSeq.Empty 
	 }
  }
}