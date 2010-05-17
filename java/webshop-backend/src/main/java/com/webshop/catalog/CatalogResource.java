
package com.webshop.catalog;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.ArrayList;

@Path("/catalog")
public class CatalogResource {

    @GET 
    @Produces("application/json")
    public ArrayList<CatalogBean> getCatalog() {
    	return(CatalogManager.getInstance().getCatalog());
    }
}
