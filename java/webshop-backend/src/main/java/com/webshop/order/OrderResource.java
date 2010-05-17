package com.webshop.order;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/order/{order}")
public class OrderResource {
    @GET
    @Produces("application/json")
    public OrderBean getOrder(@PathParam("order") String orderId) {
    	return(OrderManager.getInstance().getOrder(orderId));
    }    
}
