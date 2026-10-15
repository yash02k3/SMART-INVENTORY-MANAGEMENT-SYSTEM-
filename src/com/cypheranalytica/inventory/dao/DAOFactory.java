package com.cypheranalytica.inventory.dao;

/**
 * Factory class providing Data Access Objects (DAO).
 * Uses Java Collections Framework (ArrayList, HashMap, TreeMap) for clean in-memory storage.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Yash
 * Cypher Analytica Internship
 */
public class DAOFactory {

    private static final CategoryDAO categoryDAO = new InMemoryCategoryDAO();
    private static final SupplierDAO supplierDAO = new InMemorySupplierDAO();
    private static final ItemDAO itemDAO = new InMemoryItemDAO();
    private static final TransactionDAO transactionDAO = new InMemoryTransactionDAO();
    private static final AuditDAO auditDAO = new InMemoryAuditDAO();

    public static void initialize() {
        System.out.println(">> [STORAGE] Initialized In-Memory Collections Engine (Pre-loaded with sample inventory data).");
    }

    public static ItemDAO getItemDAO() {
        return itemDAO;
    }

    public static SupplierDAO getSupplierDAO() {
        return supplierDAO;
    }

    public static CategoryDAO getCategoryDAO() {
        return categoryDAO;
    }

    public static TransactionDAO getTransactionDAO() {
        return transactionDAO;
    }

    public static AuditDAO getAuditDAO() {
        return auditDAO;
    }
}
