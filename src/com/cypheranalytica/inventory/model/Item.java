package com.cypheranalytica.inventory.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model representing an Item / Product in the inventory.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Yash
 * Cypher Analytica Internship
 */
public class Item implements Serializable, Comparable<Item> {
    private static final long serialVersionUID = 1L;

    private int itemId;
    private String sku;
    private String name;
    private int categoryId;
    private String categoryName;
    private int quantity;
    private BigDecimal unitPrice;
    private int reorderThreshold;
    private Integer supplierId;
    private String supplierName;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Item() {
        this.unitPrice = BigDecimal.ZERO;
        this.reorderThreshold = 10;
    }

    public Item(int itemId, String sku, String name, int categoryId, String categoryName, 
                int quantity, BigDecimal unitPrice, int reorderThreshold, 
                Integer supplierId, String supplierName) {
        this.itemId = itemId;
        this.sku = sku;
        this.name = name;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.reorderThreshold = reorderThreshold;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
    }

    public Item(String sku, String name, int categoryId, int quantity, 
                BigDecimal unitPrice, int reorderThreshold, Integer supplierId) {
        this.sku = sku;
        this.name = name;
        this.categoryId = categoryId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.reorderThreshold = reorderThreshold;
        this.supplierId = supplierId;
    }

    // Business helper methods
    public boolean isLowStock() {
        return this.quantity <= this.reorderThreshold;
    }

    public BigDecimal getTotalValue() {
        if (unitPrice == null) return BigDecimal.ZERO;
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public String getStatusBadge() {
        if (quantity == 0) {
            return "OUT OF STOCK";
        } else if (quantity <= reorderThreshold) {
            return "LOW STOCK";
        } else {
            return "IN STOCK";
        }
    }

    // Getters and Setters
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getReorderThreshold() {
        return reorderThreshold;
    }

    public void setReorderThreshold(int reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public int compareTo(Item other) {
        if (other == null) return 1;
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        return String.format("[%s] %-24s | Qty: %-4d | ₹%-8.2f | Min: %-3d | %s", 
                sku, name, quantity, unitPrice, reorderThreshold, getStatusBadge());
    }
}
