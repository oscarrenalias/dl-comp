package com.webshop;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import com.webshop.catalog.CatalogBean;
import com.webshop.item.ItemBean;
import com.webshop.order.AddressInfo;
import com.webshop.order.ContactInfo;
import com.webshop.order.OrderBean;
import com.webshop.order.OrderItemBean;

@Provider
public class CustomJAXBContextResolver implements ContextResolver<JAXBContext> {

    private JAXBContext context;
    private Class[] types = {CatalogBean.class, OrderItemBean.class, ItemBean.class, AddressInfo.class, ContactInfo.class, OrderBean.class};

    public CustomJAXBContextResolver() throws Exception {
        this.context = new JSONJAXBContext(
                JSONConfiguration.natural().build(),
                types);
    }

    public JAXBContext getContext(Class<?> objectType) {
        return (types[0].equals(objectType)) ? context : null;
    }
}

