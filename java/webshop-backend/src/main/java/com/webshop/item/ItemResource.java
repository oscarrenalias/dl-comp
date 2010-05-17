package com.webshop.item;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/item/{item}")
public class ItemResource {
    @GET 
    @Produces("application/json")
    public ItemBean getItem(@PathParam("item") String itemId) {
    	return(ItemManager.getInstance().getItem(itemId));
    }
}
