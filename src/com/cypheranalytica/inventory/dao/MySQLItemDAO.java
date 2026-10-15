package com.cypheranalytica.inventory.dao;

import com.cypheranalytica.inventory.exception.DatabaseException;
import com.cypheranalytica.inventory.exception.DuplicateItemException;
import com.cypheranalytica.inventory.exception.InventoryException;
import com.cypheranalytica.inventory.model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of ItemDAO for MySQL.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Payal (Roll No. 248036)
 * Cypher Analytica Internship
 */
public class MySQLItemDAO implements ItemDAO {

    private static final String BASE_SELECT = 
            "SELECT i.item_id, i.sku, i.name, i.category_id, c.name AS category_name, " +
            "       i.quantity, i.unit_price, i.reorder_threshold, i.supplier_id, " +
            "       s.name AS supplier_name, i.created_at, i.updated_at " +
            "FROM items i " +
            "LEFT JOIN categories c ON i.category_id = c.category_id " +
            "LEFT JOIN suppliers s ON i.supplier_id = s.supplier_id ";

    @Override
    public Item save(Item item) throws InventoryException {
        String sql = "INSERT INTO items (sku, name, category_id, quantity, unit_price, reorder_threshold, supplier_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, item.getSku());
            ps.setString(2, item.getName());
            ps.setInt(3, item.getCategoryId());
            ps.setInt(4, item.getQuantity());
            ps.setBigDecimal(5, item.getUnitPrice());
            ps.setInt(6, item.getReorderThreshold());
            if (item.getSupplierId() != null) {
                ps.setInt(7, item.getSupplierId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setItemId(rs.getInt(1));
                }
            }
            return findById(item.getItemId());
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry
                throw new DuplicateItemException("SKU", item.getSku());
            }
            throw new DatabaseException("Failed to save item: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Item item) throws InventoryException {
        String sql = "UPDATE items SET sku = ?, name = ?, category_id = ?, quantity = ?, " +
                     "unit_price = ?, reorder_threshold = ?, supplier_id = ? WHERE item_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getSku());
            ps.setString(2, item.getName());
            ps.setInt(3, item.getCategoryId());
            ps.setInt(4, item.getQuantity());
            ps.setBigDecimal(5, item.getUnitPrice());
            ps.setInt(6, item.getReorderThreshold());
            if (item.getSupplierId() != null) {
                ps.setInt(7, item.getSupplierId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setInt(8, item.getItemId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new DuplicateItemException("SKU", item.getSku());
            }
            throw new DatabaseException("Failed to update item: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int itemId) throws InventoryException {
        String sql = "DELETE FROM items WHERE item_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete item: " + e.getMessage(), e);
        }
    }

    @Override
    public Item findById(int itemId) {
        String sql = BASE_SELECT + "WHERE i.item_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding item by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Item findBySku(String sku) {
        String sql = BASE_SELECT + "WHERE LOWER(i.sku) = LOWER(?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sku);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding item by SKU: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Item> findByName(String nameKeyword) {
        List<Item> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE LOWER(i.name) LIKE LOWER(?) ORDER BY i.name ASC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nameKeyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error searching item by name: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Item> findAll() {
        List<Item> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY i.name ASC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving items: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Item> findLowStockItems() {
        List<Item> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE i.quantity <= i.reorder_threshold ORDER BY i.quantity ASC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding low stock items: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Item> findByCategoryId(int categoryId) {
        List<Item> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE i.category_id = ? ORDER BY i.name ASC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding items by category: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean updateQuantity(int itemId, int newQuantity) {
        String sql = "UPDATE items SET quantity = ? WHERE item_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update item quantity: " + e.getMessage(), e);
        }
    }

    private Item mapRow(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setItemId(rs.getInt("item_id"));
        item.setSku(rs.getString("sku"));
        item.setName(rs.getString("name"));
        item.setCategoryId(rs.getInt("category_id"));
        item.setCategoryName(rs.getString("category_name"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setReorderThreshold(rs.getInt("reorder_threshold"));
        int supId = rs.getInt("supplier_id");
        if (!rs.wasNull()) {
            item.setSupplierId(supId);
            item.setSupplierName(rs.getString("supplier_name"));
        }
        item.setCreatedAt(rs.getTimestamp("created_at"));
        item.setUpdatedAt(rs.getTimestamp("updated_at"));
        return item;
    }
}
