package com.webshop.frontend.restclient

import  com.sun.jersey.api.client.filter.Filterable
import com.sun.jersey.api.client.config.ClientConfig
import com.sun.jersey.api.client._
import com.sun.jersey.client.impl.ClientRequestImpl
import _root_.java.net.URI
import javax.ws.rs.core.{Cookie, MediaType,MultivaluedMap,UriBuilder} 

/**
 * A Simplified Scala replacement for the Jersey Client WebResource class.
 *
 * This is needed because there is a bug in the scala compiler that
 * prevents the Jersey WebResource class from being used.
 *
 * see <a href="http://lampsvn.epfl.ch/trac/scala/ticket/1539">the bug report</a>
 *
 * This class does not implement UniformInterface. UniformInterface has many
 * methods that reference the GenericType class. The GenericType class
 * looks like it was added to Jersey to support a more conscise way of
 * referencing really nasty parameterized java types. Scala as the type keyword -
 * which serves the same purpose - so the GenericType class does not appear to be needed.
 *
 */

class Resource(clientHandler:ClientHandler, uri:URI) extends Filterable(clientHandler) {
 override def toString() = uri.toString()
 override def hashCode() = uri.hashCode()

 override def equals(other:Any):Boolean = other match {
 case that : Resource =>  isComparable(that) && this.uri == that.getURI()
 case _ => false
 }

 def isComparable(that:Any) = that.isInstanceOf[Resource]
 def getUriBuilder() = UriBuilder.fromUri(uri);
 def getURI() = uri

 /**
 * Create a Resource from this web resource with an additional
 * query parameter added to the URI of this web resource.
 *
 * @param key the query parameter name
 * @param value the query parameter value
 * @return the new web resource.
 */
 def queryParam(key:String , value:String ) = {
 val b = getUriBuilder();
 b.queryParam(key, value);
 new Resource(clientHandler, b.build());
 }

 /**
 * Create a new Resource from this web resource with additional
 * query parameters added to the URI of this web resource.
 *
 * todo: Creat a scala implicit that converts from MultivaluedMap to a scalafied Map?
 * @param params the query parameters.
 * @return the new web resource.
 */
 def queryParams(params: MultivaluedMap[String,String]) = {
 val b = getUriBuilder();
 val i = params.entrySet().iterator();
 while( i.hasNext() ) {
 val entry = i.next();
 val vals = entry.getValue().iterator();
 while( vals.hasNext() ) {
 b.queryParam(entry.getKey(),vals.next())
 }
 }
 new Resource(clientHandler, b.build());
 }

 /**
 * scalifed version of above
 */
 def queryParams(params: Map[String,List[String]] ) = {
 val b = getUriBuilder();
 params foreach { case(key,valueList) => valueList foreach { b.queryParam(key,_)}}
 new Resource(clientHandler, b.build());
 }

 def path(path:String) = new Resource(clientHandler, getUriBuilder().path(path).build());

 // uniform interface methods according to Roy
 def head(r:Request) =  method("HEAD",r);
 def options(r:Request) = method("OPTIONS",r)
 def get(r:Request) = method("GET",r)
 def put(r:Request) = method("PUT",r)
 def post(r:Request) = method("POST",r)
 def delete(r:Request) = method("DELETE",r)

 def method(method:String, r:Request) = {
 getHeadHandler().handle(new ClientRequestImpl(uri,method,r.entity, r.metaData))
 }
 }