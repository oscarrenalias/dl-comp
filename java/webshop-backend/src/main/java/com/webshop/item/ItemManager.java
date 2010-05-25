package com.webshop.item;

import java.util.HashMap;

public class ItemManager {

	private static ItemManager instance = null;
	
	private HashMap<String, ItemBean> items = new HashMap<String, ItemBean>();
	
	public static ItemManager getInstance() {
		if(instance == null)
			instance = new ItemManager();
		
		return(instance);
	}
	
	private ItemManager() {
		createTestData();
	}
	
	public ItemBean getItem(String id) {
		return(items.get(id));
	}
	
	private void createTestData() {
		
		System.out.println("Creating some test items");
		
		int i = 0;
		while( i++ < 50 ) {
			ItemBean item = new ItemBean();
			item.id = new Integer(i).toString();
			item.name = "Item " + i;
			item.description = "Description of item " + i;
			item.price = 100.0;
			
			// set some test images
			item.imgsSmall.add("http://pan3.fotovista.com/dev/3/2/03554423/t_03554423.jpg");
			item.imgsSmall.add("http://pan3.fotovista.com/dev/3/2/03554423/t_03554423.jpg");
			item.imgsLarge.add("http://pan3.fotovista.com/dev/3/2/03554423/u_03554423.jpg");
			item.imgsLarge.add("http://pan3.fotovista.com/dev/3/2/03554423/u_03554423.jpg");
			
			items.put(item.id, item);
		}		
	}
}
