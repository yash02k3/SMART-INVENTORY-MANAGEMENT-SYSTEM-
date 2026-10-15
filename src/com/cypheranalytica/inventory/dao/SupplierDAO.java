package com.cypheranalytica.inventory.dao;

import com.cypheranalytica.inventory.exception.InventoryException;
import com.cypheranalytica.inventory.model.Supplier;

import java.util.List;

/**
 * Data Access Object (DAO) interface for Supplier entities.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Payal (Roll No. 248036)
 * Cypher Analytica Internship
 */
public interface SupplierDAO {
    Supplier save(Supplier supplier) throws InventoryException;
    boolean update(Supplier supplier) throws InventoryException;
    boolean delete(int supplierId) throws InventoryException;
    Supplier findById(int supplierId);
    List<Supplier> findAll();
    List<Supplier> findActiveSuppliers();
}
