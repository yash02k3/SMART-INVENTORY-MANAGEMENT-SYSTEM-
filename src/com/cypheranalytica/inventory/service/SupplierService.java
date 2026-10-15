package com.cypheranalytica.inventory.service;

import com.cypheranalytica.inventory.dao.DAOFactory;
import com.cypheranalytica.inventory.dao.SupplierDAO;
import com.cypheranalytica.inventory.exception.InventoryException;
import com.cypheranalytica.inventory.model.Supplier;

import java.util.List;

/**
 * Service Layer handling Supplier operations.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Payal (Roll No. 248036)
 * Cypher Analytica Internship
 */
public class SupplierService {

    private SupplierDAO getSupplierDAO() {
        return DAOFactory.getSupplierDAO();
    }

    public Supplier addSupplier(String name, String contactPerson, String phone, String email, String address) 
            throws InventoryException {
        if (name == null || name.trim().isEmpty()) {
            throw new InventoryException("Supplier company name is required.");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new InventoryException("Supplier contact phone number is required.");
        }
        Supplier s = new Supplier(name.trim(), contactPerson != null ? contactPerson.trim() : "", 
                                  phone.trim(), email != null ? email.trim() : "", 
                                  address != null ? address.trim() : "");
        return getSupplierDAO().save(s);
    }

    public boolean updateSupplier(Supplier supplier) throws InventoryException {
        if (supplier == null || supplier.getSupplierId() <= 0) {
            throw new InventoryException("Invalid supplier ID.");
        }
        return getSupplierDAO().update(supplier);
    }

    public boolean deactivateSupplier(int supplierId) throws InventoryException {
        return getSupplierDAO().delete(supplierId);
    }

    public Supplier getSupplierById(int id) {
        return getSupplierDAO().findById(id);
    }

    public List<Supplier> getAllSuppliers() {
        return getSupplierDAO().findAll();
    }

    public List<Supplier> getActiveSuppliers() {
        return getSupplierDAO().findActiveSuppliers();
    }
}
