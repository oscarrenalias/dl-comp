package com.webshop.order;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper class for responses that include lists of orders. We use a separate
 * wrapper class so that the response is a json object with one field that contains, 
 * the list of orders, otherwise responses would be plain arrays
 */
@XmlRootElement(name="Orders")	
class OrderList {
	
	public ArrayList<OrderBean> orders = new ArrayList<OrderBean>();
		
	public void setOrders(ArrayList<OrderBean> orders) {
		this.orders = orders;
	}
}	

