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
	
    var amount = "1";

	implicit val imageBinder = ItemImageDataBinding
	
    def addToCartAjax(item:ModelItem): JsCmd = {
     		ShoppingCart.addItem(1, item)
			import net.liftweb.http.js.jquery.JqJsCmds.DisplayMessage 
			new DisplayMessage("messages", Text(S.?("Item added to shopping cart")), 5000, 1000)
    }	

    def addToCartForm(item:ModelItem) = ShoppingCart.addItem(amount.toInt, item)
	
	def apply(item: ModelItem): Binding = (xhtml: NodeSeq) => bind("item", xhtml, 
		"id" -> item.id, 
		"name" -> item.name,
		"link" -%> SHtml.link("/item", () => currentItem(Full(item)), Text("Details")),
		"add_to_cart_ajax" -%> {(ns: NodeSeq) => 
			if(User.loggedIn_?) SHtml.a(() => addToCartAjax(item), Text(S.?("Add to Cart")))
			else <span></span> },
		"description" -> item.description,
		"image" -%> <img src={item.getImage(0)} />,
		"thumbnail" -%> <img src={item.getThumbnail(0)} />,
		"images" -> item.images.flatMap(image => image.bind(chooseTemplate("item", "images", xhtml))),
		"currency" -> item.currency,
		"price" -> item.price.toString,
		"amount_to_cart_form" -> SHtml.text(amount, amount = _),
		"add_to_cart_form" -%> SHtml.submit(S.?("Add to cart"), () => addToCartForm(item)))
}

object ItemImageDataBinding extends DataBinding[ItemImageData] {
	def apply(image: ItemImageData) =
		bind("image", _, 
		"small" -%> <img src={image.small} />,
		"link" -%> <a href={image.large}><img src={image.small} /></a>, 
		"large" -%> <img src={image.large} />,
		AttrBindParam("small_src", Text(image.small), "src"),
		AttrBindParam("small_href", Text(image.small), "href"),
		AttrBindParam("large_href", Text(image.large), "href"),	
		AttrBindParam("large_src", Text(image.large), "src"))
		
}

object CatalogCategoryBinding extends DataBinding[CatalogCategory] {
	def apply(cat: CatalogCategory): Binding = bind("category", _,
		"id" -> cat.id,
		"description" -> cat.description,
		"name" -> cat.name,
		"itemcount" -> cat.numProducts,
		"link" -%> SHtml.link("/browse", () => currentCategory(Full(cat)), Text(cat.name)))
}

object ShoppingCartBinding extends DataBinding[ShoppingCart] {
	
	def apply(s: ShoppingCart): Binding = (xhtml: NodeSeq) => {
		var itemsString = ""
		if(s.items.length == 1) itemsString = S.?("item")
		else itemsString = S.?("items")
		
		bind("cart", xhtml,
			"number_of_items" -> Text(s.totalItems.toString + " " + itemsString),
			"total_price" -> Text(s.totalPrice.toString  +  "€"))
	}
}

object AddressBinding extends DataBinding[AddressInfo] {
	def apply(address: AddressInfo): Binding = bind("address", _,
		"address1" -> address.address1,
		"address2" -> address.address2,
		"city" -> address.city,
		"postcode" -> address.postcode,
		"country" -> address.country)
}

object ContactBinding extends DataBinding[ContactInfo] {
	def apply(contact: ContactInfo): Binding = bind("contact", _, 
		"email" -> contact.email,
		"phone" -> contact.phone )
}



trait OrderBinding extends DataBinding[Order] {
	
	implicit val addressBinding: DataBinding[AddressInfo]
	implicit val contactBinding: DataBinding[ContactInfo]
	
	def apply(order: Order): Binding = (xhtml: NodeSeq) => 
		bind("order", xhtml, 
			"info_link" -%> SHtml.a({() =>
				currentOrder(Full(order));
				SetHtml("order-info-" + order.id, <lift:embed what="/templates-hidden/order-data.html" />)},
				Text("Details")),
			"info_container" -> <div id={order.id}></div>,
			"id" -> order.id,						
			"status" -> order.status,
			"description" -> order.nicerDescription,
			"address" -> order.address.bind(chooseTemplate("order", "address", xhtml)),
			"contact" -> order.contact.bind(chooseTemplate("order", "contact", xhtml)) /*,
			"items" -> order.items.bind(chooseTemplate("order", "items", xhtml))*/
	)	
}

object DefaultOrderBinding extends OrderBinding {
	val addressBinding = AddressBinding
	val contactBinding = ContactBinding
}