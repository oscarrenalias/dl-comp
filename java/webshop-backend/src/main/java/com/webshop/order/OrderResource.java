package com.webshop.order;

import java.net.URI;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

@Path("/order")
public class OrderResource {
	
	@Context private UriInfo uriInfo;
    @Context private Request request;	
	
    @GET
    @Produces("application/json")
    @Path("{order}")
    public Response getOrder(@PathParam("order") String orderId) {
    	System.out.println("Received GET request orderID = " + orderId);
    	
    	OrderBean order = OrderManager.getInstance().getOrder(orderId);
    	if( order == null ) 
    		return Response.status(Response.Status.NOT_FOUND).build();
    	else
    		return Response.ok(order).build();
    }    
    
    @PUT
	@Consumes("application/json")
    @Produces("application/json")	
	@Path("{order}")
	public Response updateOrder(JAXBElement<OrderBean> order, @PathParam("order") String orderId) {
    	System.out.println("Updating order:" + orderId);
    	
    	// does the order exist?
    	OrderBean currentOrder = OrderManager.getInstance().getOrder(orderId);
    	if( currentOrder == null ) {
    		return Response.status(Response.Status.NOT_FOUND).build();
    	}
    	
    	// just in case the json entity that we just received also contains an id, we will
    	// overwrite that one with the one from the request
    	OrderBean toUpdate = order.getValue();
    	toUpdate.id = orderId;
    	OrderBean updatedOrder = OrderManager.getInstance().updateOrder(toUpdate);
    	
    	// return the URI to the updated order 
    	return Response.created(uriInfo.getRequestUri()).entity(updatedOrder).build();    	
    }
    
    @POST
	@Consumes("application/json")
    @Produces("application/json")	
	public Response createOrder(JAXBElement<OrderBean> order) {
    	System.out.println("Creating a new order...");    	
    	OrderBean newOrder = OrderManager.getInstance().addOrder(order.getValue());    	
    	System.out.println("New order number: " + newOrder.id);
    	
    	// return the URI to the created order
    	URI orderURI = uriInfo.getAbsolutePathBuilder().path(newOrder.id).build();
    	return Response.created(orderURI).entity(newOrder).build();
    }
    
    @DELETE
    @Consumes("application/json")
    @Produces("application/json")    
    @Path("{order}")    
    public Response deleteOrder(@PathParam("order") String orderId) {
    	System.out.println("Received DELETE request orderID = " + orderId);
    	
    	// check if the order exists
    	OrderBean order = OrderManager.getInstance().getOrder(orderId);
    	if( order == null ) 
    		return Response.status(Response.Status.NOT_FOUND).build();
    	
    	// if it does, mark the status as deleted and update it
    	order.status = OrderManager.ORDER_STATUS_CANCELLED;
    	OrderManager.getInstance().updateOrder(order);
    	
    	return Response.ok(order).build();
    }
}
