package com.cypheranalytica.inventory.exception;

/**
 * Base checked exception for Smart Inventory Management System.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Yash
 * Cypher Analytica Internship
 */
public class InventoryException extends Exception {
    private static final long serialVersionUID = 1L;

    public InventoryException(String message) {
        super(message);
    }

    public InventoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
