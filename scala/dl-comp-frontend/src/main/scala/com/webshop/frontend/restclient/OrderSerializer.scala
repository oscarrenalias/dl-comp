package com.webshop.frontend.restclient

import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
import net.liftweb.json.JsonAST._
import net.liftweb.json.Extraction._
import net.liftweb.json.Printer._ 
import com.webshop.frontend.model.Order

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
	 
         /*def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Interval] = {
           case (TypeInfo(IntervalClass, _), json) => json match {
             case JObject(JField("start", JInt(s)) :: JField("end", JInt(e)) :: Nil) =>
               new Interval(s.longValue, e.longValue)
             case x => throw new MappingException("Can't convert " + x + " to Interval")
           }
         }*/
 
           def serializeLineItems(o:Order): JValue = {             
             // case class that we can serialize directly, will make the job easier
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
                     JField("number", JString(o.number)) ::  
                     JField("status", JString(o.status)) ::
                     JField("description", JString(o.description)) ::  
                     JField("items", serializeLineItems(o)) :: Nil)
         }
}
