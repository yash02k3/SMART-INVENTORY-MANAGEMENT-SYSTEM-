package com.cypheranalytica.inventory.service;

import com.cypheranalytica.inventory.dao.DAOFactory;
import com.cypheranalytica.inventory.dao.ItemDAO;
import com.cypheranalytica.inventory.dao.TransactionDAO;
import com.cypheranalytica.inventory.exception.InsufficientStockException;
import com.cypheranalytica.inventory.exception.InventoryException;
import com.cypheranalytica.inventory.exception.ItemNotFoundException;
import com.cypheranalytica.inventory.model.Item;
import com.cypheranalytica.inventory.model.StockAuditLog;
import com.cypheranalytica.inventory.model.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service Layer managing inventory transactions (Stock-In and Stock-Out)
 * with strict validations, atomic updates, and audit trails.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Yash
 * Cypher Analytica Internship
 */
public class TransactionService {

    private static final AtomicLong sequence = new AtomicLong(System.currentTimeMillis() % 1000000);

    private ItemDAO getItemDAO() {
        return DAOFactory.getItemDAO();
    }

    private TransactionDAO getTransactionDAO() {
        return DAOFactory.getTransactionDAO();
    }

    private synchronized String generateTxnCode(Transaction.Type type) {
        String prefix = (type == Transaction.Type.STOCK_IN) ? "IN" : "OUT";
        return String.format("TXN-%s-%04d", prefix, sequence.incrementAndGet() % 10000);
    }

    /**
     * Records Stock Inward (procurement / restock).
     */
    public synchronized Transaction recordStockIn(int itemId, int quantity, String counter, String remarks) 
            throws InventoryException {
        if (quantity <= 0) {
            throw new InventoryException("Stock-In quantity must be greater than zero.");
        }

        Item item = getItemDAO().findById(itemId);
        if (item == null) {
            throw new ItemNotFoundException(itemId);
        }

        int oldQty = item.getQuantity();
        int newQty = oldQty + quantity;

        // 1. Update quantity
        getItemDAO().updateQuantity(itemId, newQty);
        item.setQuantity(newQty);

        // 2. Record transaction
        String txnCode = generateTxnCode(Transaction.Type.STOCK_IN);
        BigDecimal unitPrice = item.getUnitPrice();
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));

        Transaction txn = new Transaction(
                txnCode, itemId, Transaction.Type.STOCK_IN, quantity, 
                unitPrice, totalAmount, 
                counter != null && !counter.trim().isEmpty() ? counter.trim() : "Main Receiving", 
                remarks != null ? remarks.trim() : "Restock received"
        );
        txn.setItemName(item.getName());
        txn.setItemSku(item.getSku());

        Transaction savedTxn = getTransactionDAO().save(txn);

        // 3. Log audit
        DAOFactory.getAuditDAO().logChange(new StockAuditLog(
                itemId, oldQty, newQty, "STOCK_IN", counter, "Received " + quantity + " units (" + txnCode + ")"
        ));

        return savedTxn;
    }

    /**
     * Records Stock Outward (sales / dispatch / consumption).
     * Synchronized to prevent overselling during single & multi-threaded operations.
     */
    public synchronized Transaction recordStockOut(int itemId, int quantity, String counter, String remarks) 
            throws InventoryException {
        if (quantity <= 0) {
            throw new InventoryException("Stock-Out quantity must be greater than zero.");
        }

        Item item = getItemDAO().findById(itemId);
        if (item == null) {
            throw new ItemNotFoundException(itemId);
        }

        int available = item.getQuantity();
        if (available < quantity) {
            throw new InsufficientStockException(item.getName(), quantity, available);
        }

        int newQty = available - quantity;

        // 1. Update quantity
        getItemDAO().updateQuantity(itemId, newQty);
        item.setQuantity(newQty);

        // 2. Record transaction
        String txnCode = generateTxnCode(Transaction.Type.STOCK_OUT);
        BigDecimal unitPrice = item.getUnitPrice();
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));

        Transaction txn = new Transaction(
                txnCode, itemId, Transaction.Type.STOCK_OUT, quantity, 
                unitPrice, totalAmount, 
                counter != null && !counter.trim().isEmpty() ? counter.trim() : "Checkout Counter", 
                remarks != null ? remarks.trim() : "Stock sold / dispatched"
        );
        txn.setItemName(item.getName());
        txn.setItemSku(item.getSku());

        Transaction savedTxn = getTransactionDAO().save(txn);

        // 3. Log audit
        DAOFactory.getAuditDAO().logChange(new StockAuditLog(
                itemId, available, newQty, "STOCK_OUT", counter, "Dispatched " + quantity + " units (" + txnCode + ")"
        ));

        return savedTxn;
    }

    public List<Transaction> getAllTransactions() {
        return getTransactionDAO().findAll();
    }

    public List<Transaction> getRecentTransactions(int limit) {
        return getTransactionDAO().getRecentTransactions(limit);
    }

    public List<Transaction> getItemTransactions(int itemId) {
        return getTransactionDAO().findByItemId(itemId);
    }
}
