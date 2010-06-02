package com.webshop.frontend.restclient

import net.liftweb.json._
import scala.reflect.Manifest
import net.liftweb.json.Serialization.{read, write}

trait JsonSerializable {	
	def toJson: String = {
		implicit val formats = Serialization.formats(NoTypeHints)
		write(this)
	}
}
