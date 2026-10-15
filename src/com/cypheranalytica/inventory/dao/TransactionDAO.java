package com.cypheranalytica.inventory.dao;

import com.cypheranalytica.inventory.exception.InventoryException;
import com.cypheranalytica.inventory.model.Transaction;

import java.util.List;

/**
 * Data Access Object (DAO) interface for Transaction records.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Yash
 * Cypher Analytica Internship
 */
public interface TransactionDAO {
    Transaction save(Transaction transaction) throws InventoryException;
    List<Transaction> findAll();
    List<Transaction> findByItemId(int itemId);
    List<Transaction> getRecentTransactions(int limit);
}
