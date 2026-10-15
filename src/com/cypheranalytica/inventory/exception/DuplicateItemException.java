package com.cypheranalytica.inventory.exception;

/**
 * Thrown when trying to insert an item with an existing SKU or unique constraint.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Yash
 * Cypher Analytica Internship
 */
public class DuplicateItemException extends InventoryException {
    private static final long serialVersionUID = 1L;

    public DuplicateItemException(String message) {
        super(message);
    }

    public DuplicateItemException(String field, String value) {
        super("An item already exists with " + field + ": '" + value + "'.");
    }
}
