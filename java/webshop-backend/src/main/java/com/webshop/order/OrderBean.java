package com.webshop.order;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Order")
public class OrderBean {

    public String status = "New";
    public String id = "0";
    public String description;
    public String user;
    public ArrayList<OrderItemBean> items = new ArrayList<OrderItemBean>();
    
    public void insert() {
    	OrderBean newOrder = OrderManager.getInstance().addOrder(this);
    	this.id = newOrder.id;
    	this.status = newOrder.status;
    }
    
    public OrderBean get(String orderId) {
    	return(OrderManager.getInstance().getOrder(orderId));
    }
    
    public void update() {
    	
    }
    
    public void delete() {
    	
    }
}