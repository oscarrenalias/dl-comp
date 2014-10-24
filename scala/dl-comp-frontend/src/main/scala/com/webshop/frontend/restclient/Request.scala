package com.webshop.frontend.restclient

import com.sun.jersey.client.impl.ClientRequestImpl
import com.sun.jersey.core.header.OutBoundHeaders
import _root_.java.util.Locale
import _root_.java.net.URI
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

/**
 * The Request singleton has some syntactic sugar for creating Request objects
 * For example:
 *    resource.get(Request().acceptLanguageString("en")) // creates a request with a null entity
 *    resource.post(Request(myFile).mediaType("multipart/mime"))
 */
object Request {
	def apply() = new Request(null)
 	def apply(entity:Object) = new Request(entity)
 	def noEntity = new Request(null)
}

/**
 * Encapsulates a  web request
 * including the entity to be sent (optional - for example this will be null for a get)
 * and all of request cookies, accept headers, etc.
 */
class Request(val entity:Object) {
	var metaData = new OutBoundHeaders()

	def mediaType(t:MediaType) = {
		metaData.putSingle("Content-Type", t);
 		this
	}

	def mediaType(t:String):Request = mediaType(MediaType.valueOf(t))

	def accept(mediaTypes:MediaType* ) = {
		for (val m <- mediaTypes)
		add("Accept", m)
		this
	}

 	def acceptString(mediaTypes:String* ) = {
 		for (val m <- mediaTypes)
 		add("Accept", m)
 		this
 	}

 	def acceptLanguage(locales:Locale*) = {
 		for (val l <- locales)
 		add("Accept-Language", l)
 		this
 	}

 	def acceptLanguageString(locales:String*) = {
 		for (val l <- locales)
 		add("Accept-Language", l)
 		this
 	}

 	def cookie(cookie:Cookie ) = add("Cookie", cookie)

 	def header(name:String, value:Object ) = add(name, value)

 	private def add(name:String, value:Object ) = {
 		metaData.add(name,value)
 		this
 	}
}