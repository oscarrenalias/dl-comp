package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._
import _root_.net.liftweb.mapper.{DB, ConnectionManager, Schemifier, DefaultConnectionIdentifier, ConnectionIdentifier}
import _root_.java.sql.{Connection, DriverManager}
import _root_.com.webshop.frontend.model._
import _root_.javax.servlet.http.{HttpServletRequest}
import com.webshop.frontend.snippet._

/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?)
      DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)

    // where to search snippet
    LiftRules.addToPackages("com.webshop.frontend")
    
    Schemifier.schemify(true, Log.infoF _, User)
    
	MenuInfo.setSitemap
    
    /*
     * Show the spinny image when an Ajax call starts
     */
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    /*
     * Make the spinny image go away when it ends
     */
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

	LiftRules.early.append{ _.setCharacterEncoding("UTF-8") }    
    
	S.addAround(DB.buildLoanWrapper)    
  } 
}

object MenuInfo {
    
  import Loc._
  val IfLoggedIn = If(() => User.currentUser.isDefined, "You must be logged in")
  val CartHasItems = If(() => ShoppingCart.hasItems, "Nothing to check out")

  def setSitemap = {
	  // Build SiteMap
	    LiftRules.setSiteMap(SiteMap(MenuInfo.menu :_*))		
  }
  
  def menu: List[Menu] =  
	Menu(Loc("Home", List("index"), "Home", LocGroup("top-level"))) :: 
    Menu(Loc("Catalog", List("browse"), "Item catalog", LocGroup("top-level"))) ::
    Menu(Loc("CartContents", List("cart"), "Cart Contents", IfLoggedIn)) ::
    Menu(Loc("Checkout", List("checkout"), "Checkout", IfLoggedIn)) ::
    Menu(Loc("Item Details", List("item"), "Item details")) ::
    Menu(Loc("contact", List("contact", "index"), "contact", LocGroup("top-level"))) ::
    User.sitemap
}

/**
* Database connection calculation
*/
object DBVendor extends ConnectionManager {
  private var pool: List[Connection] = Nil
  private var poolSize = 0
  private val maxPoolSize = 4

  private def createOne: Box[Connection] = try {
    val driverName: String = Props.get("db.driver") openOr
    "org.apache.derby.jdbc.EmbeddedDriver"

    val dbUrl: String = Props.get("db.url") openOr
    "jdbc:derby:lift_example;create=true"

    Class.forName(driverName)

    val dm = (Props.get("db.user"), Props.get("db.password")) match {
      case (Full(user), Full(pwd)) =>
	DriverManager.getConnection(dbUrl, user, pwd)

      case _ => DriverManager.getConnection(dbUrl)
    }

    Full(dm)
  } catch {
    case e: Exception => e.printStackTrace; Empty
  }

  def newConnection(name: ConnectionIdentifier): Box[Connection] =
    synchronized {
      pool match {
	case Nil if poolSize < maxPoolSize =>
	  val ret = createOne
        poolSize = poolSize + 1
        ret.foreach(c => pool = c :: pool)
        ret

	case Nil => wait(1000L); newConnection(name)
	case x :: xs => try {
          x.setAutoCommit(false)
          Full(x)
        } catch {
          case e => try {
            pool = xs
            poolSize = poolSize - 1
            x.close
            newConnection(name)
          } catch {
            case e => newConnection(name)
          }
        }
      }
    }

  def releaseConnection(conn: Connection): Unit = synchronized {
    pool = conn :: pool
    notify
  }
}