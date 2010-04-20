package com.accenture.webshop.frontend.snippet

import _root_.net.liftweb.http._
import _root_.scala.xml.{NodeSeq,Text,Node,Elem}  
import _root_.net.liftweb.util.{Box,Full,Empty,Helpers,Log}  
import _root_.net.liftweb.util.Helpers._
import com.accenture.webshop.frontend.logging._

object userName extends RequestVar[String]("")
object password extends RequestVar[String]("")
// You can use a StatefulSnippet to avoid using these RequestVar-s 

object isLoggedIn extends SessionVar[Boolean](false)

class Login extends Logs {
  def login(xhtml:NodeSeq): NodeSeq = {
    
    
    def allowUser: Unit = {
             isLoggedIn.set(true)
             S.notice("Welcome!")
             S.redirectTo("/")      
    }    
    
    bind("f", xhtml,
       "username" -> SHtml.text("", p => userName.set(p)),
       "password" -> SHtml.password("", p => password.set(p)),
       "send" -> SHtml.submit("Login", () => {
         debug("processing login form submission")
         userName.get match {
           case "" => S.error("Invalid username or password")
           case _ => allowUser
         }        
        })
    )
  }
}
