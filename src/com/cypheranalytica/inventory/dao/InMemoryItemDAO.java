package com.cypheranalytica.inventory.dao;

import com.cypheranalytica.inventory.exception.DuplicateItemException;
import com.cypheranalytica.inventory.exception.InventoryException;
import com.cypheranalytica.inventory.model.Item;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-Memory implementation of ItemDAO using Collections (HashMap, TreeMap, ArrayList).
 * Demonstrates Collections Framework principles and provides zero-config demo support.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Yash
 * Cypher Analytica Internship
 */
public class InMemoryItemDAO implements ItemDAO {

    private final Map<Integer, Item> itemsById = new ConcurrentHashMap<>();
    private final Map<String, Integer> skuIndex = new ConcurrentHashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger(0);

    public InMemoryItemDAO() {
        seedInitialItems();
    }

    private void seedInitialItems() {
        addItemDirect(1, "1042", "USB Cable Type-C", 1, "Electronics", 4, new BigDecimal("149.00"), 10, 1, "Shreeji Traders");
        addItemDirect(2, "1077", "Wireless Mouse", 1, "Electronics", 2, new BigDecimal("499.00"), 15, 2, "TechSupply Co.");
        addItemDirect(3, "1093", "HDMI Adapter", 1, "Electronics", 6, new BigDecimal("299.00"), 12, 2, "TechSupply Co.");
        addItemDirect(4, "2210", "A4 Paper Ream", 3, "Stationery", 265, new BigDecimal("250.00"), 50, 3, "Om Stationers");
        addItemDirect(5, "2215", "Gel Pen (Pack of 10)", 3, "Stationery", 412, new BigDecimal("120.00"), 100, 3, "Om Stationers");
        addItemDirect(6, "3301", "Packing Box (Medium)", 4, "Packaging", 11, new BigDecimal("45.00"), 25, 4, "Wrapwell Ltd.");
        addItemDirect(7, "3305", "Bubble Wrap Roll", 4, "Packaging", 180, new BigDecimal("620.00"), 30, 4, "Wrapwell Ltd.");
        addItemDirect(8, "4401", "Office Chair (Mesh)", 5, "Furniture", 95, new BigDecimal("3499.00"), 10, 5, "Comfort Interiors");
        idSequence.set(8);
    }

    private void addItemDirect(int id, String sku, String name, int catId, String catName,
                               int qty, BigDecimal price, int minThreshold, Integer supId, String supName) {
        Item item = new Item(id, sku, name, catId, catName, qty, price, minThreshold, supId, supName);
        item.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        item.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        itemsById.put(id, item);
        skuIndex.put(sku.toUpperCase(), id);
    }

    @Override
    public synchronized Item save(Item item) throws InventoryException {
        if (skuIndex.containsKey(item.getSku().toUpperCase())) {
            throw new DuplicateItemException("SKU", item.getSku());
        }
        int id = idSequence.incrementAndGet();
        item.setItemId(id);
        item.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        item.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        itemsById.put(id, item);
        skuIndex.put(item.getSku().toUpperCase(), id);
        return item;
    }

    @Override
    public synchronized boolean update(Item item) throws InventoryException {
        Item existing = itemsById.get(item.getItemId());
        if (existing == null) {
            return false;
        }

        // If SKU changed, check uniqueness
        if (!existing.getSku().equalsIgnoreCase(item.getSku())) {
            if (skuIndex.containsKey(item.getSku().toUpperCase())) {
                throw new DuplicateItemException("SKU", item.getSku());
            }
            skuIndex.remove(existing.getSku().toUpperCase());
            skuIndex.put(item.getSku().toUpperCase(), item.getItemId());
        }

        item.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        itemsById.put(item.getItemId(), item);
        return true;
    }

    @Override
    public synchronized boolean delete(int itemId) throws InventoryException {
        Item removed = itemsById.remove(itemId);
        if (removed != null) {
            skuIndex.remove(removed.getSku().toUpperCase());
            return true;
        }
        return false;
    }

    @Override
    public Item findById(int itemId) {
        return itemsById.get(itemId);
    }

    @Override
    public Item findBySku(String sku) {
        if (sku == null) return null;
        Integer id = skuIndex.get(sku.trim().toUpperCase());
        return id != null ? itemsById.get(id) : null;
    }

    @Override
    public List<Item> findByName(String nameKeyword) {
        List<Item> result = new ArrayList<>();
        if (nameKeyword == null) return result;
        String query = nameKeyword.toLowerCase();
        for (Item item : itemsById.values()) {
            if (item.getName().toLowerCase().contains(query)) {
                result.add(item);
            }
        }
        result.sort(Comparator.comparing(Item::getName, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    @Override
    public List<Item> findAll() {
        // Use TreeMap to maintain sorted order by Item name
        Map<String, Item> sortedMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Item item : itemsById.values()) {
            sortedMap.put(item.getName() + "-" + item.getItemId(), item);
        }
        return new ArrayList<>(sortedMap.values());
    }

    @Override
    public List<Item> findLowStockItems() {
        List<Item> lowStockList = new ArrayList<>();
        for (Item item : itemsById.values()) {
            if (item.isLowStock()) {
                lowStockList.add(item);
            }
        }
        lowStockList.sort(Comparator.comparingInt(Item::getQuantity));
        return lowStockList;
    }

    @Override
    public List<Item> findByCategoryId(int categoryId) {
        List<Item> result = new ArrayList<>();
        for (Item item : itemsById.values()) {
            if (item.getCategoryId() == categoryId) {
                result.add(item);
            }
        }
        result.sort(Comparator.comparing(Item::getName, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    @Override
    public synchronized boolean updateQuantity(int itemId, int newQuantity) {
        Item item = itemsById.get(itemId);
        if (item != null) {
            item.setQuantity(newQuantity);
            item.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            return true;
        }
        return false;
    }
}
