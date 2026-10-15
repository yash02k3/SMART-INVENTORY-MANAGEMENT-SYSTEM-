package com.cypheranalytica.inventory.dao;

import com.cypheranalytica.inventory.exception.InventoryException;
import com.cypheranalytica.inventory.model.Transaction;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-Memory implementation of TransactionDAO using Collections (List, Queue).
 * 
 * Part of Smart Inventory Management System
 * Developed by: Yash
 * Cypher Analytica Internship
 */
public class InMemoryTransactionDAO implements TransactionDAO {

    private final List<Transaction> transactionList = new CopyOnWriteArrayList<>();
    private final AtomicInteger idSequence = new AtomicInteger(0);

    public InMemoryTransactionDAO() {
        seedInitialTransactions();
    }

    private void seedInitialTransactions() {
        addTxnDirect(1, "TXN-1001", 1, "USB Cable Type-C", "1042", Transaction.Type.STOCK_IN, 20, new BigDecimal("149.00"), new BigDecimal("2980.00"), "Warehouse Receiving", "Initial procurement batch");
        addTxnDirect(2, "TXN-1002", 1, "USB Cable Type-C", "1042", Transaction.Type.STOCK_OUT, 16, new BigDecimal("149.00"), new BigDecimal("2384.00"), "Counter 1", "Retail sales to walk-in customer");
        addTxnDirect(3, "TXN-1003", 2, "Wireless Mouse", "1077", Transaction.Type.STOCK_IN, 50, new BigDecimal("499.00"), new BigDecimal("24950.00"), "Warehouse Receiving", "Restock shipment received");
        addTxnDirect(4, "TXN-1004", 2, "Wireless Mouse", "1077", Transaction.Type.STOCK_OUT, 48, new BigDecimal("499.00"), new BigDecimal("23952.00"), "Counter 2 (Online)", "Fulfilled e-commerce orders");
        addTxnDirect(5, "TXN-1005", 4, "A4 Paper Ream", "2210", Transaction.Type.STOCK_IN, 300, new BigDecimal("250.00"), new BigDecimal("75000.00"), "Warehouse Receiving", "Bulk monthly delivery from Om Stationers");
        addTxnDirect(6, "TXN-1006", 4, "A4 Paper Ream", "2210", Transaction.Type.STOCK_OUT, 35, new BigDecimal("250.00"), new BigDecimal("8750.00"), "Corporate Desk", "Internal consumption and dispatch");
        idSequence.set(6);
    }

    private void addTxnDirect(int id, String code, int itemId, String itemName, String sku, 
                              Transaction.Type type, int qty, BigDecimal unitPrice, 
                              BigDecimal total, String counter, String remarks) {
        Transaction t = new Transaction(id, code, itemId, itemName, sku, type, qty, unitPrice, total, counter, remarks, new Timestamp(System.currentTimeMillis() - (1000L * 60 * (10 - id))));
        transactionList.add(t);
    }

    @Override
    public Transaction save(Transaction transaction) throws InventoryException {
        int id = idSequence.incrementAndGet();
        transaction.setTransactionId(id);
        if (transaction.getCreatedAt() == null) {
            transaction.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }
        transactionList.add(transaction);
        return transaction;
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> copy = new ArrayList<>(transactionList);
        copy.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return copy;
    }

    @Override
    public List<Transaction> findByItemId(int itemId) {
        List<Transaction> list = new ArrayList<>();
        for (Transaction t : transactionList) {
            if (t.getItemId() == itemId) {
                list.add(t);
            }
        }
        list.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return list;
    }

    @Override
    public List<Transaction> getRecentTransactions(int limit) {
        List<Transaction> sorted = findAll();
        if (sorted.size() <= limit) {
            return sorted;
        }
        return sorted.subList(0, limit);
    }
}
