package com.webshop.catalog;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

import com.webshop.item.*;

@XmlRootElement(name="Catalog")
public class CatalogBean {

	public CatalogBean parent;
	public String name;
	public String description;
	public ArrayList<ItemBean> items = new ArrayList<ItemBean>();
	public ArrayList<CatalogBean> children = new ArrayList<CatalogBean>();
}
