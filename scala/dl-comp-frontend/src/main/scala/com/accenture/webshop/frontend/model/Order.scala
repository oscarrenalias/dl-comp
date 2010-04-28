package com.accenture.webshop.frontend.model

import net.liftweb._ 
import mapper._ 
import http._ 
import SHtml._ 
import util._

class NonEmptyMappedString[T <: Mapper[T]](var owner: T, msg: String) extends MappedString(owner, 255) {
	 override def validations = 
		 valMinLen(1, msg) _ :: super.validations  
}
 
class Order extends LongKeyedMapper[Order] with IdPK { 
 def getSingleton = Order 
 
 object address1 extends NonEmptyMappedString(this, "Address 1 cannot be empty")
 object address2 extends NonEmptyMappedString(this, "Address 2 cannot be empty")
 object city extends NonEmptyMappedString(this, "City cannot be empty")
 object postcode extends NonEmptyMappedString(this, "Postcode cannot be empty")
 object country extends NonEmptyMappedString(this, "Country cannot be empty")
 object phone extends NonEmptyMappedString(this, "Phone cannot be empty")
} 
 
object Order extends Order with LongKeyedMetaMapper[Order]
