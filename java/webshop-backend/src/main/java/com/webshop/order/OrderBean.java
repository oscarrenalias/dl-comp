package com.webshop.order;

import java.util.ArrayList;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Order")
public class OrderBean {

    public String status = "New";
    public String id = "0";
    public String description;
    public String user;
    public AddressInfo address;
    public ContactInfo contact;
    
    public ArrayList<OrderItemBean> items = new ArrayList<OrderItemBean>();
    public Date created = new Date();
    public Date lastUpdate = new Date();    
    
    public void insert() {
    	OrderBean newOrder = OrderManager.getInstance().addOrder(this);
    	this.id = newOrder.id;
    	this.status = newOrder.status;
    }
    
    public OrderBean get(String orderId) {
    	return(OrderManager.getInstance().getOrder(orderId));
    }
    
    public void update() {
    	OrderBean updatedOrder = OrderManager.getInstance().updateOrder(this);
    	this.lastUpdate = updatedOrder.lastUpdate;
    }
    
    public void delete() {
    	
    }
}