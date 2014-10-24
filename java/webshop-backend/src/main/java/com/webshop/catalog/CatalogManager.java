package com.webshop.catalog;

import java.util.ArrayList;

import com.webshop.item.ItemManager;

public class CatalogManager {

	private static CatalogManager instance = null;

	private static ArrayList<CatalogBean> catalogData = new ArrayList<CatalogBean>();

	private CatalogManager() {
		// generate some test data
		createTestData();
	}

	private void createTestData() {

		System.out.println("Adding some test data to the catalogue");

		// some nodes
		CatalogBean e1 = new CatalogBean();
		e1.name = "Category 1";
		e1.description = "Level 1 catalog category";
		e1.parent = null;
		CatalogBean e2 = new CatalogBean();
		e2.name = "Category 2";
		e2.description = "Level 1 catalog category";
		e2.parent = null;
		// some subnodes
		CatalogBean e3 = new CatalogBean();
		e3.name = "Category 1.1";
		e3.description = "Level 1.1 catalog category";
		e3.parent = null;
		e1.children.add(e3);
		// and some items
		e1.items.add(ItemManager.getInstance().getItem("1"));
		e1.items.add(ItemManager.getInstance().getItem("2"));
		e1.items.add(ItemManager.getInstance().getItem("3"));
		e2.items.add(ItemManager.getInstance().getItem("4"));
		e2.items.add(ItemManager.getInstance().getItem("5"));
		e2.items.add(ItemManager.getInstance().getItem("1"));
		// finally add the new children to the root node
		catalogData.add(e1);
		catalogData.add(e2);
		// and the root node to t
	}

	public static CatalogManager getInstance() {
		if( instance == null )
			instance = new CatalogManager();

		return(instance);
	}

	public ArrayList<CatalogBean> getCatalog() {
		return catalogData;
	}
}
