package com.webshop.frontend.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._ 
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._

/**
 * Class wrapping around user functionality, based on Lift's user classes
 */
object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users" // define the DB table name
  override def screenWrap = Full(<lift:surround with="default" at="content">
			       <lift:bind /></lift:surround>)
  // define the order fields will appear in forms and output
  override def fieldOrder = List(id, firstName, lastName, email,
  locale, timezone, password, textArea)

  // comment this line out to require email validations
  override def skipEmailValidation = true

   override lazy val sitemap: List[Menu] =  
   List(loginMenuLoc, logoutMenuLoc, myOrdersMenuLoc, createUserMenuLoc,  
        lostPasswordMenuLoc, resetPasswordMenuLoc,  
        editUserMenuLoc, changePasswordMenuLoc,  
        ).flatten(a => a)

   override def loginMenuLoc: Box[Menu] = {  
     Full(Menu(Loc("Login", loginPath, S.??("login"),  
				   LocGroup("user-operations"),
                   If(notLoggedIn_? _, S.??("already.logged.in")),  
                   Template(() => wrapIt(login)))))  
   }

  override def logoutMenuLoc: Box[Menu] =  
   Full(Menu(Loc("Logout", logoutPath, S.??("logout"),  
				   LocGroup("user-operations"),
                 Template(() => wrapIt(logout)),  
                 testLogginIn)))  
   
   /** 
    * The menu item for creating the user/sign up (make this "Empty" to disable) 
    */  
   override def createUserMenuLoc: Box[Menu] =  
   Full(Menu(Loc("CreateUser", signUpPath,  
                 S.??("sign.up"),  
				   LocGroup("user-operations"),
                 Template(() => wrapIt(signupFunc.map(_()) openOr signup)),  
                 If(notLoggedIn_? _, S.??("logout.first")))))  
   
   /** 
    * The menu item for lost password (make this "Empty" to disable) 
    */  
   override def lostPasswordMenuLoc: Box[Menu] =  
   Full(Menu(Loc("LostPassword", lostPasswordPath,  
                 S.??("lost.password"),  
				   LocGroup("user-operations"),
                 Template(() => wrapIt(lostPassword)),  
                 If(notLoggedIn_? _, S.??("logout.first"))))) // not logged in  
   
   /** 
    * The menu item for resetting the password (make this "Empty" to disable) 
    */  
   override def resetPasswordMenuLoc: Box[Menu] =  
   Full(Menu(Loc("ResetPassword", (passwordResetPath, true),  
                 S.??("reset.password"), 
  				LocGroup("user-operations"), Hidden,  
                 Template(() => wrapIt(passwordReset(snarfLastItem))),  
                 If(notLoggedIn_? _,  
                    S.??("logout.first"))))) //not Logged in  
   
   /** 
    * The menu item for editing the user (make this "Empty" to disable) 
    */  
   override def editUserMenuLoc: Box[Menu] =  
   Full(Menu(Loc("EditUser", editPath, S.??("edit.user"),  
				   LocGroup("user-operations"),
                 Template(() => wrapIt(editFunc.map(_()) openOr edit)),  
                 testLogginIn)))  
   
   /** 
    * The menu item for changing password (make this "Empty" to disable) 
    */  
   override def changePasswordMenuLoc: Box[Menu] =  
   Full(Menu(Loc("ChangePassword", changePasswordPath,  
                 S.??("change.password"),  
				   LocGroup("user-operations"),
                 Template(() => wrapIt(changePassword)),  
                 testLogginIn)))  
   
   /** 
    * The menu item for validating a user (make this "Empty" to disable) 
    */  
   override def validateUserMenuLoc: Box[Menu] =  
   Full(Menu(Loc("ValidateUser", (validateUserPath, true),  
                 S.??("validate.user"), Hidden,  
                 Template(() => wrapIt(validateUser(snarfLastItem))),  
                 If(notLoggedIn_? _, S.??("logout.first")))))

	def myOrdersMenuLoc: Box[Menu] =
   Full(Menu(Loc("myorders", List("myorders"), S.?("Order History"),  
				   LocGroup("user-operations"),  
                 testLogginIn)))	

	/**
	 * Overridden method to add some testing information
	 */
	override def loginXhtml = {
			    (<form method="post" action={S.uri}><div class="center_title_bar">{S.??("Login")}</div><table>
					  <tr><td colspan="2"><b>Use a@a.com and 12345678 as the password for testing purposes</b></td></tr> 
			          <tr><td>{S.??("email.address")}</td><td><user:email /></td></tr>
			          <tr><td>{S.??("password")}</td><td><user:password /></td></tr>
			          <tr><td><a href={lostPasswordPath.mkString("/", "/", "")}
			                >{S.??("recover.password")}</a></td><td><user:submit /></td></tr></table>
			     </form>)
			  }
}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User] {
  def getSingleton = User // what's the "meta" server

  // define an additional field for a personal essay
  object textArea extends MappedTextarea(this, 2048) {
    override def textareaRows  = 10
    override def textareaCols = 50
    override def displayName = "Personal Essay"
  }
}

