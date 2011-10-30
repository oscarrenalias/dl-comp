package com.webshop.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import com.webshop.item.ItemManager;

public class OrderManager {

	final static String ORDER_STATUS_NEW = "New";
	final static String ORDER_STATUS_CREATED = "Created";
	final static String ORDER_STATUS_DELIVERED = "Delivered";
	final static String ORDER_STATUS_CANCELLED = "Cancelled";

	final static int HIGHEST_ORDER_NUMBER_RANGE = 99999999;

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
    		if(i % 2 == 0) order.user = "user@user.com"; else order.user = "user2@user2.com";
    		AddressInfo address = new AddressInfo();
    		address.address1 = "Address 1";
    		address.address2 = "Address 2";
    		address.city = "City";
    		address.country = "Country";
    		address.postcode = "postcode";
    		order.address = address;
    		ContactInfo contact = new ContactInfo();
    		contact.phone = "phone";
    		contact.name = "Name";
    		contact.email = "a@a.com";
    		order.contact = contact;
    		// add line items
    		OrderItemBean item = new OrderItemBean();
    		item.setData(ItemManager.getInstance().getItem("1"), 5);
    		order.items.add(item);
    		// add line items
			if(i % 2 == 0) {
	    		OrderItemBean item2 = new OrderItemBean();
	    		item2.setData(ItemManager.getInstance().getItem("1"), 15);
	    		order.items.add(item2);
			}

    		// add the order to the global list
    		addOrderWithId(order);
    		System.out.println("order id: " + order.id);
    	}
    }

	public synchronized OrderBean addOrderWithId(OrderBean order) {
		// save the status
		order.status = ORDER_STATUS_CREATED;
		orders.put(order.id, order);

		return(order);
	}

	public synchronized OrderBean addOrder(OrderBean order) {
		order.id = generateOrderId();
		return(addOrderWithId(order));
	}

	public synchronized OrderBean updateOrder(OrderBean order) {
		// save the status
		order.lastUpdate = new Date();
		orders.put(order.id, order);
		return(order);
	}

	private String generateOrderId() {
		String orderId = null;
		boolean valid = false;

		while(!valid) {
			orderId = Integer.toString(new Random().nextInt(HIGHEST_ORDER_NUMBER_RANGE));
			// is it already being used?
			valid = orders.containsKey(orderId) ? false : true;
		}
		return(orderId);
	}

	public OrderBean getOrder(String orderId) {
		return(orders.get(orderId));
	}

	public ArrayList<OrderBean> getOrders() {
		return new ArrayList<OrderBean>();
	}

	/**
	 * Returns all the orders that belong to the given user
	 * @param user
	 * @return
	 */
	public ArrayList<OrderBean> getUserOrders(String user) {
		ArrayList<OrderBean> userOrders = new ArrayList<OrderBean>();
		for(OrderBean o : orders.values()) {
			if(o.user.equals(user))
				userOrders.add(o);
		}
		return(userOrders);
	}
}