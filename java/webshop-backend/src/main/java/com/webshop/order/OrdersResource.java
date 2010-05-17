
package com.webshop.order;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import java.util.ArrayList;


// The Java class will be hosted at the URI path "/myresource"
@Path("/orders")
public class OrdersResource {

    @GET 
    @Produces("application/json")
    public ArrayList<OrderBean> getOrders() {
    	return(OrderManager.getInstance().getOrders());
    }
}
