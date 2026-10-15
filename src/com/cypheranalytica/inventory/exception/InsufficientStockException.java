package com.cypheranalytica.inventory.exception;

/**
 * Thrown when a stock-out / purchase request exceeds available quantity in inventory.
 * Prevents overselling in single-threaded and multithreaded transactions.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Yash
 * Cypher Analytica Internship
 */
public class InsufficientStockException extends InventoryException {
    private static final long serialVersionUID = 1L;

    private final int requested;
    private final int available;

    public InsufficientStockException(String itemName, int requested, int available) {
        super(String.format("Insufficient stock for '%s'. Requested: %d, Available: %d units.",
                itemName, requested, available));
        this.requested = requested;
        this.available = available;
    }

    public int getRequested() {
        return requested;
    }

    public int getAvailable() {
        return available;
    }
}
