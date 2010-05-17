package com.webshop.order;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement()
public class OrderBean {

    public String status = "New";
    public String id = "0";
    public String description;
    public String user;
    public ArrayList<OrderItemBean> items = new ArrayList<OrderItemBean>();
}