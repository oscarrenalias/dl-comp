package com.webshop.frontend.model

import com.webshop.frontend.restclient.RestClient

object Catalog {
   var data = Map(
     1 -> new CatalogCategory("1","Category 1","Description of category 1",null,List(),List("1", "2")),
     2 -> new CatalogCategory("2","Category 2","Description of category 2",null,List(3, 4),List("1", "2", "3")),
     3 -> new CatalogCategory("3","Category 3","Description of category 3","2",List(),List("4")),
     4 -> new CatalogCategory("4","Category 4","Description of category 4","2",List(),List("5")),
   )
   
   def getLevel(level: String) = data.filter(c => c._2.parent == level)   
   
   def getRoot = getLevel(null)
   
   def getItems(level: String): Option[List[Item]] = {
     data.get(level.toInt) match {
       case None => None
       case Some(c) => Some(c.items.flatMap(RestClient.Items.get(_)))
     }
   }
   
   def getCategory(id: Int) = data.get(id)
}

case class CatalogCategory(var id:String,
						   var name:String, 
                           var description:String, 
                           var parent:String, 
                           var children: List[Int], 
                           var items:List[String]) {
	
  /**
   * Returns a list of the CatalogCategory objects under this one
   */
  def getChildren: List[CatalogCategory] = children.flatMap({ x => Catalog.getCategory(x)})
}

object RootCatalogCategory extends CatalogCategory("0", "Root", "Root Category", null, List(), List()) {
  override def getChildren: List[CatalogCategory] = Catalog.getLevel(null).values.toList
}