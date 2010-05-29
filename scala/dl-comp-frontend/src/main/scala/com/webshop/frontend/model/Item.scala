package com.webshop.frontend.model

import _root_.net.liftweb.common.{Box,Full,Empty} 
import _root_.net.liftweb.util.Log
import com.webshop.frontend.restclient.RestClient
import com.webshop.WebshopConfig

/**
 * Bean that holds information about an item  
 */
case class Item(var id: String, 
				var name: String, 
				var description: String, 
				var price: String, 
				var currency: String,
				var images: List[ItemImageData]) {

  def getThumbnail(id: Int) = {
    if (images.isDefinedAt(id)) images(id).small else WebshopConfig.THUMBNAIL_NOT_AVAILABLE
  }
  
  def getThumbnails = for (image <- images) yield image.small
  
  def getImage(id: Int) = {
    if (images.isDefinedAt(id)) images(id).large else WebshopConfig.IMAGE_NOT_AVAILABLE
  }
  
  def getImages = for (image <- images) yield image.large
}

object Item {      
  def get(id: String): Box[Item] = RestClient.Items.get(id)
}

case class ItemImageData (var small: String, var large: String)
