package com.cypheranalytica.inventory.dao;

import com.cypheranalytica.inventory.exception.InventoryException;
import com.cypheranalytica.inventory.model.Item;

import java.util.List;

/**
 * Data Access Object (DAO) interface for Item entities.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Payal (Roll No. 248036)
 * Cypher Analytica Internship
 */
public interface ItemDAO {
    Item save(Item item) throws InventoryException;
    boolean update(Item item) throws InventoryException;
    boolean delete(int itemId) throws InventoryException;
    Item findById(int itemId);
    Item findBySku(String sku);
    List<Item> findByName(String nameKeyword);
    List<Item> findAll();
    List<Item> findLowStockItems();
    List<Item> findByCategoryId(int categoryId);
    boolean updateQuantity(int itemId, int newQuantity);
}
