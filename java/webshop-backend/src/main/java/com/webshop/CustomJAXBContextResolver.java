package com.webshop;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import com.webshop.item.ItemBean;
import com.webshop.order.OrderBean;
import com.webshop.order.OrderItemBean;
import com.webshop.order.OrderList;

/**
 * Custom JAXB context resolver that enables the "natural" json generator. The
 * natural generator has nicer features, such as generating json arrays always
 * properly, instead of only when the array has more than one element (makes
 * the parsing easier)
 */
@Provider
public class CustomJAXBContextResolver implements ContextResolver<JAXBContext> {

    private JAXBContext context;
    private Class[] types = {OrderBean.class, OrderItemBean.class, ItemBean.class, OrderList.class};

    public CustomJAXBContextResolver() throws Exception {
        this.context = new JSONJAXBContext(JSONConfiguration.natural().build(), types);
    }

    public JAXBContext getContext(Class<?> objectType) {
    	for(Class c : types) {
    		if(c.equals(objectType))
    			return(context);
    	}
    	return(null);
    }
}

