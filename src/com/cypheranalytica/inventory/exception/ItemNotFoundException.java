package com.cypheranalytica.inventory.exception;

/**
 * Thrown when an item is searched, updated, or removed but does not exist in inventory.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Payal (Roll No. 248036)
 * Cypher Analytica Internship
 */
public class ItemNotFoundException extends InventoryException {
    private static final long serialVersionUID = 1L;

    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(int itemId) {
        super("Item with ID " + itemId + " was not found in the inventory system.");
    }

    public ItemNotFoundException(String identifierType, String identifierValue) {
        super("Item with " + identifierType + " '" + identifierValue + "' was not found.");
    }
}
