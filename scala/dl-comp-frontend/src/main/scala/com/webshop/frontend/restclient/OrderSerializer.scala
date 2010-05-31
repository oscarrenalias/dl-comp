package com.webshop.frontend.restclient

import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.Extraction._
import net.liftweb.json.Printer._
import net.liftweb.json.JsonParser._
import net.liftweb.common.{Box,Full,Failure,Empty}
import scala.collection.mutable.ArrayBuffer
import com.webshop.frontend.model._
import com.webshop.frontend.snippet.ShoppingCart


/**
 * Special json serializer class so that we can serialize the Order
 * class to json.
 * 
 * If we were running the most recent snapshot of Lift (later than 2.0-M5), we could
 * just plug the class below into the custom serializer framework but since we're not there
 * yet (and this feature is not even releaseed as of 28.05.2010, we'll have to do it manually)
 * Look in class RestClient.Order.create
 */
//class OrderSerializer extends Serializer[Order] {
class OrderSerializer {
	 
         /*def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Order] = {
           case (TypeInfo(classOf[Order], _), json) => json match {
             case JObject(JField("start", JInt(s)) :: JField("end", JInt(e)) :: Nil) =>
               new Interval(s.longValue, e.longValue)
             case x => throw new MappingException("Can't convert " + x + " to Interval")
           }
         }*/
  
         private def getFieldAsString(json: JValue, field:String): String = {
           (json \ field \ classOf[JString]).firstOption.getOrElse("")
         }
         
         private def processLineItems(json: JValue): ArrayBuffer[ShoppingCart.ShoppingCartLineItem] = {
        	val itemInfo = for {                                   
        		JField("amount",JInt(amount)) <- json
        		JField("id",JString(id)) <- json
        	} yield (id, amount)
         
        	val items = itemInfo.removeDuplicates.flatMap( x => List((x._2, Item.get(x._1).get)))
        	var results = new ArrayBuffer[ShoppingCart.ShoppingCartLineItem] 
        	for( lineItem <- items ) results += (lineItem._1.intValue, lineItem._2)
         
        	results
         }
           
         def deserialize(data: String): Box[Order] = {
           
           var o = new Order
           try {
        	   // get the json string parsed into the intermediate structure
        	   val json = parse(data)
        	   // and now extract the fields from the json stream and assign them to the object
        	   o.user = getFieldAsString(json, "user")
        	   o.number = getFieldAsString(json, "id")
        	   o.status = getFieldAsString(json, "status")
        	   o.address1(getFieldAsString(json, "address1"))
        	   o.address2(getFieldAsString(json, "address2"))
        	   o.city(getFieldAsString(json, "city"))
        	   o.postcode(getFieldAsString(json, "postcode"))
        	   o.country(getFieldAsString(json, "country"))
        	   o.phone(getFieldAsString(json, "phone"))
        	   // process the line items
        	   o.items = processLineItems(json)
        	   
        	   Full(o)
           } catch {
             case e:Exception => Failure("Error parsing json string: " + data, Full(e), Empty)
           }           
         }
 
          private def serializeLineItems(o:Order): JValue = {             
             // case classes that we can serialize directly, will make the job easier
             case class LineItemInfo(var id:String)
             case class LineItemData(var item:LineItemInfo, var amount:Int)
             
             val items = for(item <- o.items) yield(new LineItemData(new LineItemInfo(item._2.id), item._1))
            
             implicit val formats = Serialization.formats(NoTypeHints)
             decompose(items.toList)
           }            

         def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
           
           case o: Order =>
             JObject(JField("address1", JString(o.address1)) :: 
                     JField("address2",   JString(o.address2)) :: 
                     JField("city", JString(o.city)) ::
                     JField("postcode", JString(o.postcode)) ::
                     JField("phone", JString(o.phone)) ::  
                     JField("user", JString(o.user)) ::
                     JField("country", JString(o.country)) :: 
                     JField("id", JString(o.number)) ::  
                     JField("status", JString(o.status)) ::
                     JField("description", JString(o.description)) ::  
                     JField("items", serializeLineItems(o)) :: Nil)
         }
}
