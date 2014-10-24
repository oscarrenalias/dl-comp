package com.webshop.frontend.snippet.bindings

import scala.xml.{NodeSeq,Text,Node,Elem}
import net.liftweb.common.{Box,Full,Empty,Failure}
import net.liftweb.http._
import net.liftweb.util.Helpers._
import com.webshop.frontend.model.{Item=>ModelItem,_}
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js._
import com.webshop.frontend.snippet.bindings.Bindings._

/**
 * Binding logic for the Item class.
 *
 * Provides some presentation logic for adding items to the shopping cart
 */
object ItemBinding extends DataBinding[ModelItem] {

    var amount = "1";

	// implicit variable that defines which binding class to use for ItemImageData
	// objects
	implicit val imageBinder = ItemImageDataBinding

    def addToCartAjax(item:ModelItem) = {
     	ShoppingCart.addItem(1, item)
		S.notice(S.?("Item added to shopping cart"))
		import net.liftweb.http.js.JsCmds._Noop
		//new DisplayMessage("messages", Text(S.?("Item added to shopping cart")), 5000, 1000)

		// we need to return a JsCmd but since we've already set the message via S.notice
		// and we don't want to do anything else, we return this "no-op" response
		_Noop
    }

    def addToCartForm(item:ModelItem) = {
		ShoppingCart.addItem(amount.toInt, item)
		S.notice(S.?("Item added to shopping cart"))
	}

	def apply(item: ModelItem): Binding = (xhtml: NodeSeq) => bind("item", xhtml,
		"id" -> item.id,
		"name" -> item.name,
		"details_link" -%> SHtml.link("/item", () => currentItem(Full(item)), Text("Details")),
		"link" -%> SHtml.link("/item", () => currentItem(Full(item)), Text(item.description)),
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

/**
 * Binding for the ItemImageData object
 */
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

/**
 * Binding logic for CatalogCategory objects
 */
object CatalogCategoryBinding extends DataBinding[CatalogCategory] {
	def apply(cat: CatalogCategory): Binding = bind("category", _,
		"id" -> cat.id,
		"description" -> cat.description,
		"name" -> cat.name,
		"itemcount" -> cat.numProducts,
		"link" -%> SHtml.link("/browse", () => currentCategory(Full(cat)), Text(cat.name)))
}

/**
 * Binding logic for the ShoppingCart data
 */
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

/**
 * Binding logic for AddressInfo objects
 */
object AddressBinding extends DataBinding[AddressInfo] {
	def apply(address: AddressInfo): Binding = bind("address", _,
		"address1" -> address.address1,
		"address2" -> address.address2,
		"city" -> address.city,
		"postcode" -> address.postcode,
		"country" -> address.country)
}

/**
 * Binding lgoci for ContactInfo objects
 */
object ContactBinding extends DataBinding[ContactInfo] {
	def apply(contact: ContactInfo): Binding = bind("contact", _,
		"email" -> contact.email,
		"phone" -> contact.phone )
}

/**
 * Binding logic for line items in Order objects
 */
object OrderLineItemBinding extends DataBinding[LineItemInfo] {

	// implicit used to define the binding class to be used for the ItemImageData objects
	implicit val imageBinder = ItemImageDataBinding

	def apply(l: LineItemInfo): Binding = (xhtml: NodeSeq) => {
		bind("line", xhtml,
			"amount" -> l.amount,
			"item" -> { (ns: NodeSeq) =>
				l.boxedItem match {
					case Full(item) => {
						implicit val itemBinding = ItemBinding
						item.bind(chooseTemplate("line", "item", xhtml))
					}
					case _ => Text("Item data not found")
				}
			}
		)
	}
}

/**
 * Trait that defines the binding logic for orders. Note how the composability
 * and implicit features are used to define that "some" class will provide
 * an implicit reference to a data binding class for addresses, contacts,
 * items and line items
 *
 * Since this class is a trait, it cannot be instantiated and it must be extended
 * so that all the required implicit parameters are provided as object/class
 * references
 */
trait OrderBinding extends DataBinding[Order] {

	implicit val addressBinding: DataBinding[AddressInfo]
	implicit val contactBinding: DataBinding[ContactInfo]
	implicit val itemBinding: DataBinding[ModelItem]
	implicit val lineItemBinding: DataBinding[LineItemInfo]

	def apply(order: Order): Binding = (xhtml: NodeSeq) =>
		bind("order", xhtml,
			"details_link" -> SHtml.link("/order", () => currentOrder(Full(order)), Text(order.id)),
			AttrBindParam("link", SHtml.link("/order", () => currentOrder(Full(order)), Text(order.id)), "href"),
			"info_container" -> <div id={order.id}></div>,
			"id" -> order.id,
			"status" -> order.status,
			"description" -> order.nicerDescription,
			"address" -> order.address.bind(chooseTemplate("order", "address", xhtml)),
			"contact" -> order.contact.bind(chooseTemplate("order", "contact", xhtml)),
			"num_items" -> order.items.length.toString,
			"total_items" -> order.getTotalItems.toString,
			"total_price" -> order.getTotalPrice.toString,
			"lines" -> order.items.flatMap(item => item.bind(chooseTemplate("order", "lines", xhtml)))
	)
}

/**
 * Object that contains the default implicit bindings for displaying
 * all the data within an Order object, extending the trait
 */
object DefaultOrderBinding extends OrderBinding {
	val addressBinding = AddressBinding
	val contactBinding = ContactBinding
	val itemBinding = ItemBinding
	val lineItemBinding = OrderLineItemBinding
}