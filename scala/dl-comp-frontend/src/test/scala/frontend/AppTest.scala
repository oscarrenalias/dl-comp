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
  // def testKO() = assertTrue(false);

  /**
   * Tests to make sure the project's XML files are well-formed.
   *
   * Finds every *.html and *.xml file in src/main/webapp (and its
   * subdirectories) and tests to make sure they are well-formed.
   */
//  def testXml() = {
//    var failed: List[File] = Nil
//
//    def handledXml(file: String) =
//      file.endsWith(".xml")
//
//    def handledXHtml(file: String) =
//      file.endsWith(".html") || file.endsWith(".htm") || file.endsWith(".xhtml")
//
//    def wellFormed(file: File) {
//      if (file.isDirectory)
//        for (f <- file.listFiles) wellFormed(f)
//
//      if (file.isFile && handledXml(file.getName)) {
//        try {
//          XML.loadFile(file)
//        } catch {
//          case e: _root_.org.xml.sax.SAXParseException => failed = file :: failed
//        }
//      }
//      if (file.isFile && handledXHtml(file.getName)) {
//        PCDataXmlParser(new java.io.FileInputStream(file.getAbsolutePath)) match {
//          case Full(_) => // file is ok
//          case _ => failed = file :: failed
//        }
//      }
//    }
//
//    wellFormed(new File("src/main/webapp"))
//
//    val numFails = failed.size
//    if (numFails > 0) {
//      val fileStr = if (numFails == 1) "file" else "files"
//      val msg = "Malformed XML in " + numFails + " " + fileStr + ": " + failed.mkString(", ")
//      println(msg)
//      fail(msg)
//    }
//  }
}
