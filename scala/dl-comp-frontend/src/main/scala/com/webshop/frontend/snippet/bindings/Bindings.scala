package com.webshop.frontend.snippet.bindings

import net.liftweb.http.TemplateFinder.findAnyTemplate
import net.liftweb.common.{Box,Full,Empty,Failure}
import net.liftweb.http._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.jquery.JqJsCmds._ 
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js._
import net.liftweb.util.Helpers._
import com.webshop.frontend.snippet.bindings.Bindings._
import scala.xml._

/**
 * This class implements composable and generic data binding for model classes 
 * based on implicits. 
 *
 * The usage of implicits allows to call model.bind() in objects that don't have such
 * but for which an implicit data binding class has been defined in the scope.
 *
 * The composability allows to provide binding logic for complex model objects, which
 * may have internal references to other "bindable" model objects.
 *
 * Having separate classes for binding allows us to reuse the binding logic throughout
 * several snippets, instead of littering the code with the same calls to flatMap and bind
 * over and over again.
 */
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