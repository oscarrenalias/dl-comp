package com.webshop.item;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

import com.webshop.catalog.CatalogBean;

@XmlRootElement(name="Item")
public class ItemBean {
	public String id;
	public String name;
	public String description;
	public Double price;
	public CatalogBean parent = null;
	public String currency = "EUR";
	public ArrayList<ItemBeanImageData> images = new ArrayList<ItemBeanImageData>();
}