package com.webshop.frontend.model

import _root_.net.liftweb.common.{Box,Full,Empty}
import _root_.net.liftweb.util.Log
import com.webshop.frontend.restclient.RestClient
import com.webshop.frontend.restclient.JsonSerializable
import com.webshop.WebshopConfig

/**
 * Bean class that holds information about an item. It is used by the Jersey client
 * code to map a REST response from the server to an Item object.
 *
 * Also provides some basic accessor methods.
 */
case class Item(var id: String,
				var name: String,
				var description: String,
				var price: String,
				var currency: String,
				var images: List[ItemImageData]) extends JsonSerializable {

  def getThumbnail(id: Int) = {
    if (images.isDefinedAt(id)) images(id).small else WebshopConfig.THUMBNAIL_NOT_AVAILABLE
  }

  def getThumbnails = for (image <- images) yield image.small

  def getImage(id: Int) = {
    if (images.isDefinedAt(id)) images(id).large else WebshopConfig.IMAGE_NOT_AVAILABLE
  }

  def getImages = for (image <- images) yield image.large
}

/**
 * Bean class used to map image data (thumbnail and large images) from REST
 * to internal objects
 */
case class ItemImageData (var small: String, var large: String)

/**
 * Singleton class to obtain item data via a REST call:
 *
 * <pre>
 *  val item = Item.get(itemId)
 * </pre>
 */
object Item extends JsonSerializable {
  def get(id: String): Box[Item] = RestClient.Items.get(id)
}