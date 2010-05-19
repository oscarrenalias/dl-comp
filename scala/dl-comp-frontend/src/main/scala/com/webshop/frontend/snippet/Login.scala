package com.webshop.frontend.snippet

import _root_.net.liftweb.http._
import _root_.scala.xml.{NodeSeq,Text,Node,Elem}  
import _root_.net.liftweb.util.{Box,Full,Empty,Helpers,Log}  
import _root_.net.liftweb.util.Helpers._
import com.webshop.frontend.logging._
import com.webshop.frontend.model._

class Login {  
  def loggedIn(xhtml:NodeSeq): NodeSeq = {
    if (User.loggedIn_?) xhtml else NodeSeq.Empty
  }
  
  def loggedOut(xhtml:NodeSeq): NodeSeq = {
    if (!User.loggedIn_?) xhtml else NodeSeq.Empty 
  }      
}