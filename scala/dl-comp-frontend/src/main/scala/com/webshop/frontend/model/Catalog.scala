package com.webshop.frontend.model

import com.webshop.frontend.restclient.RestClient
import _root_.net.liftweb.common.{Box,Full,Empty} 

object Catalog {
   var data = Map(
     1 -> new CatalogCategory(1,"Category 1","Description of category 1",0,List("1", "2")),
     2 -> new CatalogCategory(2,"Category 2","Description of category 2",0,List("1", "2", "3")),
     3 -> new CatalogCategory(3,"Category 2.1","Description of category 2.1",2,List("4")),
     4 -> new CatalogCategory(4,"Category 2.2","Description of category 2.2",2,List("5")),
	 5 -> new CatalogCategory(5,"Category 2.2.1","Description of category 2.2.1",4,List("10", "15")),
 	 6 -> new CatalogCategory(6,"Category 2.2.1.1","Description of category 2.2.1.1",5,List("20", "25"))
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
}

object RootCatalogCategory extends CatalogCategory(0, "Root", "Root Category", 0, List()) {
  override def getChildren: List[CatalogCategory] = Catalog.getLevel(0).values.toList
}