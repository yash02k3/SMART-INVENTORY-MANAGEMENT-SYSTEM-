package com.cypheranalytica.inventory.dao;

import com.cypheranalytica.inventory.exception.DatabaseException;
import com.cypheranalytica.inventory.model.StockAuditLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of AuditDAO for MySQL.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Payal (Roll No. 248036)
 * Cypher Analytica Internship
 */
public class MySQLAuditDAO implements AuditDAO {

    @Override
    public void logChange(StockAuditLog auditLog) {
        String sql = "INSERT INTO stock_audit_log (item_id, old_quantity, new_quantity, change_type, performed_by, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, auditLog.getItemId());
            ps.setInt(2, auditLog.getOldQuantity());
            ps.setInt(3, auditLog.getNewQuantity());
            ps.setString(4, auditLog.getChangeType());
            ps.setString(5, auditLog.getPerformedBy());
            ps.setString(6, auditLog.getNotes());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Warning: Audit log entry failed: " + e.getMessage());
        }
    }

    @Override
    public List<StockAuditLog> findAll() {
        List<StockAuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM stock_audit_log ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving audit logs: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<StockAuditLog> findByItemId(int itemId) {
        List<StockAuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM stock_audit_log WHERE item_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving item audit logs: " + e.getMessage(), e);
        }
        return list;
    }

    private StockAuditLog mapRow(ResultSet rs) throws SQLException {
        StockAuditLog log = new StockAuditLog();
        log.setAuditId(rs.getInt("audit_id"));
        log.setItemId(rs.getInt("item_id"));
        log.setOldQuantity(rs.getInt("old_quantity"));
        log.setNewQuantity(rs.getInt("new_quantity"));
        log.setChangeType(rs.getString("change_type"));
        log.setPerformedBy(rs.getString("performed_by"));
        log.setNotes(rs.getString("notes"));
        log.setCreatedAt(rs.getTimestamp("created_at"));
        return log;
    }
}
