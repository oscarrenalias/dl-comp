package com.webshop.order;

import com.webshop.item.ItemBean;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="item")
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