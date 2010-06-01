package com.webshop.order;

import com.webshop.item.ItemBean;
import com.webshop.item.ItemManager;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="item")
public class OrderItemBean {
	
	public String item;
	public int amount;
	
	public OrderItemBean() {
		// nothing
	}
	
	public void setData( ItemBean item, int amount ) {
		this.item = item.id;
		this.amount = amount;		
	}
	
	public ItemBean getItem() {
		return(ItemManager.getInstance().getItem(item));
	}
}