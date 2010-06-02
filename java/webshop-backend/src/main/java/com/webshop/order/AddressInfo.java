package com.webshop.order;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Order")
public class AddressInfo {
    public String address1;
    public String address2;
    public String city;
    public String country;
    public String postcode;
}
