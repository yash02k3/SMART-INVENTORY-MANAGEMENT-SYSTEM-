package com.cypheranalytica.inventory.dao;

import com.cypheranalytica.inventory.model.StockAuditLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-Memory implementation of AuditDAO using Collections.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Payal (Roll No. 248036)
 * Cypher Analytica Internship
 */
public class InMemoryAuditDAO implements AuditDAO {

    private final List<StockAuditLog> auditLogs = new CopyOnWriteArrayList<>();
    private final AtomicInteger idSequence = new AtomicInteger(0);

    @Override
    public void logChange(StockAuditLog auditLog) {
        int id = idSequence.incrementAndGet();
        auditLog.setAuditId(id);
        auditLogs.add(auditLog);
    }

    @Override
    public List<StockAuditLog> findAll() {
        List<StockAuditLog> list = new ArrayList<>(auditLogs);
        list.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return list;
    }

    @Override
    public List<StockAuditLog> findByItemId(int itemId) {
        List<StockAuditLog> list = new ArrayList<>();
        for (StockAuditLog log : auditLogs) {
            if (log.getItemId() == itemId) {
                list.add(log);
            }
        }
        list.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return list;
    }
}
