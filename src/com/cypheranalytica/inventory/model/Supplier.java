package com.cypheranalytica.inventory.model;

import java.io.Serializable;

/**
 * Model representing an Inventory Supplier.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Yash
 * Cypher Analytica Internship
 */
public class Supplier implements Serializable {
    private static final long serialVersionUID = 1L;

    private int supplierId;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private boolean active = true;

    public Supplier() {
    }

    public Supplier(int supplierId, String name, String contactPerson, String phone, String email, String address, boolean active) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.active = active;
    }

    public Supplier(String name, String contactPerson, String phone, String email, String address) {
        this.name = name;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.active = true;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (Contact: %s, Ph: %s, Email: %s)", 
                supplierId, name, contactPerson, phone, email);
    }
}
