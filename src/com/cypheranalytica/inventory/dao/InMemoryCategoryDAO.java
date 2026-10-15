package com.cypheranalytica.inventory.dao;

import com.cypheranalytica.inventory.exception.InventoryException;
import com.cypheranalytica.inventory.model.Category;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-Memory implementation of CategoryDAO using Java Collections Framework (Map, List).
 * 
 * Part of Smart Inventory Management System
 * Developed by: Yash
 * Cypher Analytica Internship
 */
public class InMemoryCategoryDAO implements CategoryDAO {

    private final Map<Integer, Category> categoryMap = new ConcurrentHashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger(0);

    public InMemoryCategoryDAO() {
        seedInitialCategories();
    }

    private void seedInitialCategories() {
        saveCategoryDirect(1, "Electronics", "Electronic gadgets, accessories, and peripherals");
        saveCategoryDirect(2, "Hardware", "Hardware tools, fasteners, and structural cables");
        saveCategoryDirect(3, "Stationery", "Office paperwork, pens, stationery supplies");
        saveCategoryDirect(4, "Packaging", "Packing boxes, bubble wraps, tapes");
        saveCategoryDirect(5, "Furniture", "Office chairs, tables, and ergonomic furniture");
        idSequence.set(5);
    }

    private void saveCategoryDirect(int id, String name, String desc) {
        Category c = new Category(id, name, desc);
        categoryMap.put(id, c);
    }

    @Override
    public Category save(Category category) throws InventoryException {
        int id = idSequence.incrementAndGet();
        category.setCategoryId(id);
        categoryMap.put(id, category);
        return category;
    }

    @Override
    public Category findById(int categoryId) {
        return categoryMap.get(categoryId);
    }

    @Override
    public Category findByName(String name) {
        if (name == null) return null;
        for (Category c : categoryMap.values()) {
            if (c.getName().equalsIgnoreCase(name.trim())) {
                return c;
            }
        }
        return null;
    }

    @Override
    public List<Category> findAll() {
        List<Category> list = new ArrayList<>(categoryMap.values());
        list.sort(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }
}
