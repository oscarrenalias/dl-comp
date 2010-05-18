package com.webshop.item;

import javax.xml.bind.annotation.XmlRootElement;
import com.webshop.catalog.*;

@XmlRootElement(name="Item")
public class ItemBean {
	public String id;
	public String name;
	public String description;
	public Double price;
	public CatalogBean parent = null;
	public String currency = "EUR";
}
