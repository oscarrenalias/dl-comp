package com.webshop.order;

import com.webshop.item.ItemBean;

public class OrderItemBean {
	public ItemBean item;
	public int amount;
	
	public OrderItemBean() {
		// nothing
	}
	
	public void setData( ItemBean item, int amount ) {
		this.item = item;
		this.amount = amount;		
	}
}