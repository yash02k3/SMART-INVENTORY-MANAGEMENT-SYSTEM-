package com.cypheranalytica.inventory.dao;

import com.cypheranalytica.inventory.exception.DatabaseException;
import com.cypheranalytica.inventory.exception.InventoryException;
import com.cypheranalytica.inventory.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of TransactionDAO for MySQL.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Payal (Roll No. 248036)
 * Cypher Analytica Internship
 */
public class MySQLTransactionDAO implements TransactionDAO {

    private static final String BASE_SELECT = 
            "SELECT t.*, i.name AS item_name, i.sku AS item_sku " +
            "FROM transactions t " +
            "JOIN items i ON t.item_id = i.item_id ";

    @Override
    public Transaction save(Transaction t) throws InventoryException {
        String sql = "INSERT INTO transactions (txn_code, item_id, txn_type, quantity, unit_price, total_amount, counter_name, remarks) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getTxnCode());
            ps.setInt(2, t.getItemId());
            ps.setString(3, t.getTxnType().name());
            ps.setInt(4, t.getQuantity());
            ps.setBigDecimal(5, t.getUnitPrice());
            ps.setBigDecimal(6, t.getTotalAmount());
            ps.setString(7, t.getCounterName());
            ps.setString(8, t.getRemarks());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    t.setTransactionId(rs.getInt(1));
                }
            }
            return t;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY t.created_at DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving transactions: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Transaction> findByItemId(int itemId) {
        List<Transaction> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE t.item_id = ? ORDER BY t.created_at DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error searching transactions: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Transaction> getRecentTransactions(int limit) {
        List<Transaction> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY t.created_at DESC LIMIT ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching recent transactions: " + e.getMessage(), e);
        }
        return list;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setTransactionId(rs.getInt("transaction_id"));
        t.setTxnCode(rs.getString("txn_code"));
        t.setItemId(rs.getInt("item_id"));
        t.setItemName(rs.getString("item_name"));
        t.setItemSku(rs.getString("item_sku"));
        t.setTxnType(Transaction.Type.valueOf(rs.getString("txn_type")));
        t.setQuantity(rs.getInt("quantity"));
        t.setUnitPrice(rs.getBigDecimal("unit_price"));
        t.setTotalAmount(rs.getBigDecimal("total_amount"));
        t.setCounterName(rs.getString("counter_name"));
        t.setRemarks(rs.getString("remarks"));
        t.setCreatedAt(rs.getTimestamp("created_at"));
        return t;
    }
}
