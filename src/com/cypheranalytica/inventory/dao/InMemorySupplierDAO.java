package com.cypheranalytica.inventory.dao;

import com.cypheranalytica.inventory.exception.InventoryException;
import com.cypheranalytica.inventory.model.Supplier;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-Memory implementation of SupplierDAO using Java Collections Framework.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Yash
 * Cypher Analytica Internship
 */
public class InMemorySupplierDAO implements SupplierDAO {

    private final Map<Integer, Supplier> supplierMap = new ConcurrentHashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger(0);

    public InMemorySupplierDAO() {
        seedInitialSuppliers();
    }

    private void seedInitialSuppliers() {
        saveDirect(1, "Shreeji Traders", "Mahesh Patel", "+91 9826012345", "info@shreejitraders.com", "124 Jawahar Marg, Indore, MP");
        saveDirect(2, "TechSupply Co.", "Ankit Sharma", "+91 9876543210", "sales@techsupply.in", "Plot 45 Electronic Complex, Indore, MP");
        saveDirect(3, "Om Stationers", "Ramesh Gupta", "+91 9425098765", "orders@omstationers.com", "12 M.G. Road, Indore, MP");
        saveDirect(4, "Wrapwell Ltd.", "Sunita Verma", "+91 9811223344", "contact@wrapwell.com", "88 Pithampur Industrial Area, MP");
        saveDirect(5, "Comfort Interiors", "Rajeev Mehra", "+91 9922334455", "support@comfortinteriors.com", "Sector B Sanwer Road, Indore, MP");
        idSequence.set(5);
    }

    private void saveDirect(int id, String name, String contact, String phone, String email, String address) {
        Supplier s = new Supplier(id, name, contact, phone, email, address, true);
        supplierMap.put(id, s);
    }

    @Override
    public Supplier save(Supplier supplier) throws InventoryException {
        int id = idSequence.incrementAndGet();
        supplier.setSupplierId(id);
        supplierMap.put(id, supplier);
        return supplier;
    }

    @Override
    public boolean update(Supplier supplier) throws InventoryException {
        if (!supplierMap.containsKey(supplier.getSupplierId())) {
            return false;
        }
        supplierMap.put(supplier.getSupplierId(), supplier);
        return true;
    }

    @Override
    public boolean delete(int supplierId) throws InventoryException {
        Supplier s = supplierMap.get(supplierId);
        if (s != null) {
            s.setActive(false);
            return true;
        }
        return false;
    }

    @Override
    public Supplier findById(int supplierId) {
        return supplierMap.get(supplierId);
    }

    @Override
    public List<Supplier> findAll() {
        List<Supplier> list = new ArrayList<>(supplierMap.values());
        list.sort(Comparator.comparing(Supplier::getName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    @Override
    public List<Supplier> findActiveSuppliers() {
        List<Supplier> list = new ArrayList<>();
        for (Supplier s : supplierMap.values()) {
            if (s.isActive()) {
                list.add(s);
            }
        }
        list.sort(Comparator.comparing(Supplier::getName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }
}
