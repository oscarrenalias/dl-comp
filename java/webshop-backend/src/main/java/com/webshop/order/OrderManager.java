package com.webshop.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.webshop.item.ItemManager;

public class OrderManager {
	
	final static String ORDER_STATUS_NEW = "New";
	final static String ORDER_STATUS_CREATED = "Created";
	final static String ORDER_STATUS_DELIVERED = "Delivered";

    public HashMap<String,OrderBean> orders = new HashMap<String,OrderBean>();    
    private static OrderManager instance = null;
    
    private OrderManager() {
    	addTestData();
    }
            
    public static OrderManager getInstance() {
    	if( instance == null )
    		instance = new OrderManager();
    	
    	return(instance);
    }
    
    private void addTestData() {
    	int i = 0;
    	System.out.println("Adding test orders");    	
    	while( i++ < 10 ) {    		
    		OrderBean order = new OrderBean();
    		order.id = new Integer(i).toString();
    		order.status = "Processed";
    		order.description = "Description of order " + order.id;
    		order.user = "user@user.com";
    		// add one line item
    		OrderItemBean item = new OrderItemBean();
    		item.setData(ItemManager.getInstance().getItem("1"), 5);
    		order.items.add(item);
    		
    		// add the order to the global list
    		addOrder(order);
    	}
    }

	public synchronized OrderBean addOrder(OrderBean order) {
		if(order.id == "0") {
			// the order has no id yet, so we should come up with one
			order.id = generateOrderId();
		}
		// save the status
		order.status = ORDER_STATUS_CREATED;
		orders.put(order.id, order);
		
		return(order);
	}
	
	private String generateOrderId() {
		String orderId = Integer.toString(new Random().nextInt());
		return(orderId);
	}
	
	public OrderBean getOrder(String orderId) {
		return(orders.get(orderId));
	}
	
	public ArrayList<OrderBean> getOrders() {
		return new ArrayList<OrderBean>();
	}
}