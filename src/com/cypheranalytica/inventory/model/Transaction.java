package com.cypheranalytica.inventory.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model representing an Inventory Transaction (Stock-In or Stock-Out).
 * 
 * Part of Smart Inventory Management System
 * Developed by: Payal (Roll No. 248036)
 * Cypher Analytica Internship
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        STOCK_IN,
        STOCK_OUT
    }

    private int transactionId;
    private String txnCode;
    private int itemId;
    private String itemName;
    private String itemSku;
    private Type txnType;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String counterName;
    private String remarks;
    private Timestamp createdAt;

    public Transaction() {
    }

    public Transaction(int transactionId, String txnCode, int itemId, String itemName, 
                       String itemSku, Type txnType, int quantity, BigDecimal unitPrice, 
                       BigDecimal totalAmount, String counterName, String remarks, Timestamp createdAt) {
        this.transactionId = transactionId;
        this.txnCode = txnCode;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemSku = itemSku;
        this.txnType = txnType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.counterName = counterName;
        this.remarks = remarks;
        this.createdAt = createdAt;
    }

    public Transaction(String txnCode, int itemId, Type txnType, int quantity, 
                       BigDecimal unitPrice, BigDecimal totalAmount, String counterName, String remarks) {
        this.txnCode = txnCode;
        this.itemId = itemId;
        this.txnType = txnType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.counterName = counterName;
        this.remarks = remarks;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getTxnCode() {
        return txnCode;
    }

    public void setTxnCode(String txnCode) {
        this.txnCode = txnCode;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemSku() {
        return itemSku;
    }

    public void setItemSku(String itemSku) {
        this.itemSku = itemSku;
    }

    public Type getTxnType() {
        return txnType;
    }

    public void setTxnType(Type txnType) {
        this.txnType = txnType;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("%s | %-10s | Item #%d (%s) | Qty: %-4d | ₹%-8.2f | %s (%s)",
                txnCode, txnType, itemId, itemName != null ? itemName : "N/A", quantity, totalAmount, counterName, remarks);
    }
}
