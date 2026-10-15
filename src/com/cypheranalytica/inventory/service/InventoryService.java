package com.cypheranalytica.inventory.service;

import com.cypheranalytica.inventory.dao.DAOFactory;
import com.cypheranalytica.inventory.dao.CategoryDAO;
import com.cypheranalytica.inventory.dao.ItemDAO;
import com.cypheranalytica.inventory.exception.DuplicateItemException;
import com.cypheranalytica.inventory.exception.InventoryException;
import com.cypheranalytica.inventory.exception.ItemNotFoundException;
import com.cypheranalytica.inventory.model.Category;
import com.cypheranalytica.inventory.model.Item;
import com.cypheranalytica.inventory.model.StockAuditLog;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service Layer handling core inventory operations, validations, and business logic.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Yash
 * Cypher Analytica Internship
 */
public class InventoryService {

    private ItemDAO getItemDAO() {
        return DAOFactory.getItemDAO();
    }

    private CategoryDAO getCategoryDAO() {
        return DAOFactory.getCategoryDAO();
    }

    public Item addItem(String sku, String name, int categoryId, int initialQty, 
                        BigDecimal unitPrice, int reorderThreshold, Integer supplierId) 
            throws InventoryException {

        if (sku == null || sku.trim().isEmpty()) {
            throw new InventoryException("SKU code cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InventoryException("Item name cannot be empty.");
        }
        if (initialQty < 0) {
            throw new InventoryException("Initial quantity cannot be negative.");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new InventoryException("Unit price must be zero or positive.");
        }
        if (reorderThreshold < 0) {
            throw new InventoryException("Reorder threshold cannot be negative.");
        }

        // Check if category exists
        Category cat = getCategoryDAO().findById(categoryId);
        if (cat == null) {
            throw new InventoryException("Invalid Category ID: " + categoryId);
        }

        // Check SKU duplication
        Item existing = getItemDAO().findBySku(sku.trim());
        if (existing != null) {
            throw new DuplicateItemException("SKU", sku);
        }

        Item item = new Item(sku.trim(), name.trim(), categoryId, initialQty, unitPrice, reorderThreshold, supplierId);
        item.setCategoryName(cat.getName());

        Item saved = getItemDAO().save(item);

        // Audit log
        DAOFactory.getAuditDAO().logChange(new StockAuditLog(
                saved.getItemId(), 0, initialQty, "INITIAL_ENTRY", "System", "New item created: " + name
        ));

        return saved;
    }

    public boolean updateItem(Item item) throws InventoryException {
        if (item == null || item.getItemId() <= 0) {
            throw new InventoryException("Invalid item reference for update.");
        }
        Item existing = getItemDAO().findById(item.getItemId());
        if (existing == null) {
            throw new ItemNotFoundException(item.getItemId());
        }

        boolean updated = getItemDAO().update(item);
        if (updated && existing.getQuantity() != item.getQuantity()) {
            DAOFactory.getAuditDAO().logChange(new StockAuditLog(
                    item.getItemId(), existing.getQuantity(), item.getQuantity(), 
                    "MANUAL_ADJUSTMENT", "Admin", "Quantity modified directly via Item Update"
            ));
        }
        return updated;
    }

    public boolean deleteItem(int itemId) throws InventoryException {
        Item existing = getItemDAO().findById(itemId);
        if (existing == null) {
            throw new ItemNotFoundException(itemId);
        }
        boolean deleted = getItemDAO().delete(itemId);
        if (deleted) {
            DAOFactory.getAuditDAO().logChange(new StockAuditLog(
                    itemId, existing.getQuantity(), 0, 
                    "DELETED", "Admin", "Item removed from inventory catalog: " + existing.getName()
            ));
        }
        return deleted;
    }

    public Item getItemById(int itemId) throws ItemNotFoundException {
        Item item = getItemDAO().findById(itemId);
        if (item == null) {
            throw new ItemNotFoundException(itemId);
        }
        return item;
    }

    public Item getItemBySku(String sku) throws ItemNotFoundException {
        Item item = getItemDAO().findBySku(sku);
        if (item == null) {
            throw new ItemNotFoundException("SKU", sku);
        }
        return item;
    }

    public List<Item> searchItems(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllItems();
        }
        String trimmed = query.trim();
        // Check if query is an exact SKU match
        Item bySku = getItemDAO().findBySku(trimmed);
        if (bySku != null) {
            return List.of(bySku);
        }
        // Otherwise search by name keyword
        return getItemDAO().findByName(trimmed);
    }

    public List<Item> getAllItems() {
        return getItemDAO().findAll();
    }

    public List<Item> getLowStockAlerts() {
        return getItemDAO().findLowStockItems();
    }

    public List<Category> getAllCategories() {
        return getCategoryDAO().findAll();
    }

    public Category getCategoryById(int id) {
        return getCategoryDAO().findById(id);
    }
}
