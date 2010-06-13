package com.webshop.frontend.snippet.bindings

import net.liftweb.http.TemplateFinder.findAnyTemplate
import net.liftweb.common.{Box,Full,Empty,Failure}
import net.liftweb.http._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.jquery.JqJsCmds._ 
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js._
import net.liftweb.util.Helpers._
import Bindings._
import scala.xml._

object Bindings {
   type Binding = NodeSeq => NodeSeq

   type DataBinding[T] = T => NodeSeq => NodeSeq

   //adds a bind() function to an object if an implicit DataBinding is available for that object
   implicit def binder[T](t: T)(implicit binding: DataBinding[T]): Binder = Binder(binding(t))
   implicit def binder(binding: Binding): Binder = Binder(binding)

   //decorator for a binding function that allows it to be called as bind() rather than apply()
   //and also provides facilities for binding to a specific template
   case class Binder(val binding: Binding) {
       def bind(xhtml: NodeSeq): NodeSeq = binding.apply(xhtml)
       def bind(templatePath: List[String]): NodeSeq = {
           findAnyTemplate(templatePath) map binding match {
               case Full(xhtml) => xhtml
               case Failure(msg, ex, _) => Text(ex.map(_.getMessage).openOr(msg))
               case Empty => Text("Unable to find template with path " + templatePath.mkString("/", "/", ""))
           }
       }
   }

   object NullBinding extends Binding {
       override def apply(xhtml : NodeSeq) : NodeSeq = NodeSeq.Empty
   }

   object StringBinding extends DataBinding[String] {
       override def apply(msg: String) = (xhtml: NodeSeq) => Text(msg)
   }

   implicit def path2list(s: String) = new {
     def path: List[String] = s.split("/").toList
   }
}