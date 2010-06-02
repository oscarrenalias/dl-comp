
package com.webshop.order;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/orders")
public class OrdersResource {
	
	/**
	 * Retrieves all the users of a given user
	 * @param user
	 * @return
	 */
    @GET
    @Produces("application/json")
    @Path("/user/{user}")
    public OrderList getUserOrders(@PathParam("user") String user) {    	
    	OrderList l = new OrderList();
    	l.setOrders(OrderManager.getInstance().getUserOrders(user));    	
    	return(l);
    }
}
