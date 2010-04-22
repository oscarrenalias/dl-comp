package com.accenture.webshop.frontend.snippet

import _root_.net.liftweb.http._
import _root_.scala.xml.{NodeSeq,Text,Node,Elem}  
import _root_.net.liftweb.util.{Box,Full,Empty,Helpers,Log}  
import _root_.net.liftweb.util.Helpers._
import com.accenture.webshop.frontend.logging._
import com.accenture.webshop.frontend.model._

class Login extends Logs {
  def login(xhtml:NodeSeq): NodeSeq = {
    
    var user = ""
    var pass = ""
    
    def auth: Unit = {
    		debug("user = " + user)
         user match {
           case "" => S.error("Invalid username or password")
           case _ => {
    		userName.set(user)
    		password.set(pass)
            User.setLoggedIn(true)
            debug("Starting user session..." + User.loggedIn)            
            S.notice("Welcome, " + userName.get + "!")
            S.redirectTo("/")                          
           }
         }           
    }    
    
    bind("f", xhtml,
       "username" -> SHtml.text(user, user = _),
       "password" -> SHtml.password(pass, pass = _),
       "send" -> SHtml.submit("Login", () => {
         debug("processing login form submission")
         auth
        })
    )
  }
  
  def getUserName: NodeSeq = {    
	Text(User.getUserName)
  }
  
  def loggedIn(xhtml:NodeSeq): NodeSeq = {
    User.loggedIn match {
      case true => xhtml
      case _ => Text("")
    }
  }
  
  def loggedOut(xhtml:NodeSeq): NodeSeq = {
    User.loggedIn match {
      case false => xhtml
      case _ => Text("")
    }
  }      
}
