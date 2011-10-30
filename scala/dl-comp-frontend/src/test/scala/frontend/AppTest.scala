package frontend

import _root_.java.io.File
import _root_.junit.framework._
import Assert._
import _root_.scala.xml.XML
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonAST
import net.liftweb.json.JsonDSL._
import com.webshop.frontend.restclient._
import com.webshop.frontend.model._

object AppTest {
  def suite: Test = {
    val suite = new TestSuite(classOf[AppTest])
    suite.addTestSuite(classOf[JsonTests])
    suite
  }

  def main(args : Array[String]) {
    _root_.junit.textui.TestRunner.run(suite)
  }
}

class JsonTests extends TestCase("json") {
	def testEmptyOrder() = {
		import com.webshop.frontend.model.Order

		val expected = """{"items":[],"contact":{"email":"","phone":"","name":""},"address":{"postcode":"","country":"","city":"","address2":"","address1":""},"status":"","user":"","description":"","id":""}"""
		val o = Order()
		assertEquals(expected, o.toJson)
	}
}

/**
 * Unit test for simple App.
 */
class AppTest extends TestCase("app") {

  /**
   * Rigourous Tests :-)
   */
  def testOK() = assertTrue(true)
}
