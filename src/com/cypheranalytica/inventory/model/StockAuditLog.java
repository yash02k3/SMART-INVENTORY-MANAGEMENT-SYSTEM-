package com.cypheranalytica.inventory.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model representing an Audit Log entry for quantity adjustments.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Yash
 * Cypher Analytica Internship
 */
public class StockAuditLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private int auditId;
    private int itemId;
    private int oldQuantity;
    private int newQuantity;
    private String changeType;
    private String performedBy;
    private String notes;
    private Timestamp createdAt;

    public StockAuditLog() {
    }

    public StockAuditLog(int auditId, int itemId, int oldQuantity, int newQuantity, 
                         String changeType, String performedBy, String notes, Timestamp createdAt) {
        this.auditId = auditId;
        this.itemId = itemId;
        this.oldQuantity = oldQuantity;
        this.newQuantity = newQuantity;
        this.changeType = changeType;
        this.performedBy = performedBy;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public StockAuditLog(int itemId, int oldQuantity, int newQuantity, 
                         String changeType, String performedBy, String notes) {
        this.itemId = itemId;
        this.oldQuantity = oldQuantity;
        this.newQuantity = newQuantity;
        this.changeType = changeType;
        this.performedBy = performedBy;
        this.notes = notes;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public int getAuditId() {
        return auditId;
    }

    public void setAuditId(int auditId) {
        this.auditId = auditId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getOldQuantity() {
        return oldQuantity;
    }

    public void setOldQuantity(int oldQuantity) {
        this.oldQuantity = oldQuantity;
    }

    public int getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(int newQuantity) {
        this.newQuantity = newQuantity;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("[%s] Item #%d: %d -> %d (%s) by %s: %s",
                createdAt, itemId, oldQuantity, newQuantity, changeType, performedBy, notes);
    }
}
