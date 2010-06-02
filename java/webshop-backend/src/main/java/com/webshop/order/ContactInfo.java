package com.webshop.order;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Contact")
public class ContactInfo {
    public String phone;
    public String name;
    public String email;
}
