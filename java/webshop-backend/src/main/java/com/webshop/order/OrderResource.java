package com.webshop.order;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.xml.bind.JAXBElement;


@Path("/order")
public class OrderResource {
	
    @GET
    @Produces("application/json")
    @Path("{order}")
    public OrderBean getOrder(@PathParam("order") String orderId) {
    	System.out.println("Received GET request orderID = " + orderId);
    	return(OrderManager.getInstance().getOrder(orderId));
    }    
    
    @PUT
	@Consumes("application/json")
	public OrderBean updateOrder(JAXBElement<OrderBean> order) {
    	return(new OrderBean());
    }
    
    @POST
	@Consumes("application/json")
	public OrderBean createOrder(JAXBElement<OrderBean> order) {
    	System.out.println("Creating a new order...");    	
    	OrderBean newOrder = OrderManager.getInstance().addOrder(order.getValue());
    	System.out.println("New order number: " + newOrder.id);
    	return(newOrder);
    }
}
