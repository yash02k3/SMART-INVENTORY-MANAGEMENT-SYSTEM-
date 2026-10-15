package com.cypheranalytica.inventory.dao;

import com.cypheranalytica.inventory.exception.InventoryException;
import com.cypheranalytica.inventory.model.Category;

import java.util.List;

/**
 * Data Access Object (DAO) interface for Category entities.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Payal (Roll No. 248036)
 * Cypher Analytica Internship
 */
public interface CategoryDAO {
    Category save(Category category) throws InventoryException;
    Category findById(int categoryId);
    Category findByName(String name);
    List<Category> findAll();
}
