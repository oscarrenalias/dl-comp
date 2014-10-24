package com.webshop.frontend.restclient

import net.liftweb.json._
import scala.reflect.Manifest
import net.liftweb.json.Serialization.{read, write}

/**
 * Trait that provides basic json serialization functionality by providing the toJson
 * method
 */
trait JsonSerializable {
	def toJson: String = {
		implicit val formats = Serialization.formats(NoTypeHints)
		write(this)
	}
}
