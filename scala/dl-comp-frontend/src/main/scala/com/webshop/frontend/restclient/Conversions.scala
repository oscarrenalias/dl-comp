package com.webshop.frontend.restclient

import com.sun.jersey.api.client.{ClientResponse,UniformInterfaceException}
import java.io.InputStream

/**
 * Implicit Conversions from a ClientResponse to another type
 * Use with caution!
 * To use this, import com.my2do.jersey.client.Conversions._

 */

object Conversions {
     implicit def response2String(r:ClientResponse):String =  getClientResponseEntityAs(r,classOf[String])
     implicit def response2InputStream(r:ClientResponse):InputStream = r.getEntityInputStream()
     implicit def response2ByteArray(r:ClientResponse):Array[Byte] =  getClientResponseEntityAs(r,classOf[Array[Byte]])
     // todo: What others do we need?

    /**
     * unwrap the entity - thrown an exception if the entity is not valid
     */
     def getClientResponseEntityAs[T](r:ClientResponse, c:Class[T]):T = {
         if( r.getStatus() >= 300 )
            throw new UniformInterfaceException(r)
         r.getEntity(c)
     }
}
