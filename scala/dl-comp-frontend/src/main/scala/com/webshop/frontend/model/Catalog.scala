package com.webshop.frontend.model

import com.webshop.frontend.restclient.RestClient
import _root_.net.liftweb.common.{Box,Full,Empty} 

object Catalog {
   var data = Map(	
     1 -> new CatalogCategory(1, "Xbox 360","Xbox 360 consoles, games and accessories", 0, List("1" ,"2", "3")),
		4->new CatalogCategory(4, "Games","Xbox 360 games", 1, List("1" ,"2", "3")),
			5->new CatalogCategory(5, "Racing games","Xbox 360 racing games", 4, List("1" ,"2", "3")),
			6->new CatalogCategory(6, "Shooters","Xbox 360 shooters", 4, List("1" ,"2", "3")),
			7->new CatalogCategory(7, "Sports","Xbox 360 sports games", 4, List("1" ,"2", "3")),
		8->new CatalogCategory(8, "Accessories","Xbox 360 accessories", 1, List("1" ,"2", "3")),	
     2 -> new CatalogCategory(2, "Playstation 3","Playstation 3 consoles, games and accessories", 0, List("1" ,"2", "3")),
		9->new CatalogCategory(9, "Games","Playstation 3 games", 2, List("1" ,"2", "3")),
			10->new CatalogCategory(10, "Racing games","Xbox 360 racing games", 9, List("1" ,"2", "3")),
			11->new CatalogCategory(11, "Shooters","Playstation 3 shooters", 9, List("1" ,"2", "3")),
			12->new CatalogCategory(12, "Sports","Playstation 3 sports games", 9, List("1" ,"2", "3")),
		18->new CatalogCategory(18,"Accessories","Playstation 3 accessories", 2, List("1" ,"2", "3")),
     3 -> new CatalogCategory(3,"Nintendo Wii","Nintendo Wii consoles, games and accessories", 0, List("1" ,"2", "3")),
		13->new CatalogCategory(13, "Games","Nintendo Wii games", 3, List("1" ,"2", "3")),
			14->new CatalogCategory(14, "Racing games","Nintendo Wii racing games", 13, List("1" ,"2", "3")),
			15->new CatalogCategory(15, "Family","Nintendo Wii family ga,es", 13, List("1" ,"2", "3")),
			16->new CatalogCategory(16, "Sports","Nintendo Wii sports games", 13, List("1" ,"2", "3")),
		17->new CatalogCategory(17,"Accessories","Nintendo Wii accessories", 3, List("1" ,"2", "3"))
   )
   
   def getLevel(level: Int) = data.filter(c => c._2.parent == level)
   
   def getRoot = getLevel(0)
   
   def getItems(level: Int): Option[List[Item]] = {
		data.get(level) match {
			case None => None
			case Some(c) => Some(c.items.flatMap(RestClient.Items.get(_)))
     	}
   }
   
   def getCategory(id: Int) = data.get(id)

   def getCategories(level: Int) = getLevel(level)

   def getChildren(id: Int):List[CatalogCategory] = data.filter({ x=> x._2.parent == id}).values.toList
}

case class CatalogCategory(var id:Int,
						   var name:String, 
                           var description:String, 
                           var parent:Int, 
                           var items:List[String]) {
	
  /**
   * Returns a list of the CatalogCategory objects under this one
   */
  def getChildren = Catalog.getChildren(this.id)
  
  /**
   * Returns the parent category, None if none is found
   */
  def getParent: Option[CatalogCategory] = if (parent == 0) None else Catalog.getCategory(parent)
  
  /**
   * Returs a list with all the parent categories up to the top one
   */
	def getParents: List[CatalogCategory] = {
		val parent = getParent
		parent match {                                                  
			case None => List()
			case Some(x) => List(x) ::: x.getParents
		}
	}
	
	/**
	 * number of products under this category
	 */
	lazy val numProducts = items.length 
}

object RootCatalogCategory extends CatalogCategory(0, "Home", "Root Category", 0, List()) {
  override def getChildren: List[CatalogCategory] = Catalog.getLevel(0).values.toList
}