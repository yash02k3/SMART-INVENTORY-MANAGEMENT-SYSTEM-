package com.cypheranalytica.inventory.dao;

import com.cypheranalytica.inventory.model.StockAuditLog;

import java.util.List;

/**
 * Data Access Object (DAO) interface for Stock Audit Logs.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Yash
 * Cypher Analytica Internship
 */
public interface AuditDAO {
    void logChange(StockAuditLog auditLog);
    List<StockAuditLog> findAll();
    List<StockAuditLog> findByItemId(int itemId);
}
