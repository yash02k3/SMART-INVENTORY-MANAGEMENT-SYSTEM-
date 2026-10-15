package com.cypheranalytica.inventory.service;

import com.cypheranalytica.inventory.dao.DAOFactory;
import com.cypheranalytica.inventory.model.Category;
import com.cypheranalytica.inventory.model.Item;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Service Layer responsible for analytics, summary reports, 
 * category aggregation, and export functionality.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Yash
 * Cypher Analytica Internship
 */
public class ReportService {

    public static class CategorySummary {
        private final String categoryName;
        private int itemCount;
        private int totalUnits;
        private BigDecimal totalValue = BigDecimal.ZERO;
        private int lowStockCount;

        public CategorySummary(String categoryName) {
            this.categoryName = categoryName;
        }

        public void addItem(Item item) {
            this.itemCount++;
            this.totalUnits += item.getQuantity();
            this.totalValue = this.totalValue.add(item.getTotalValue());
            if (item.isLowStock()) {
                this.lowStockCount++;
            }
        }

        public String getCategoryName() { return categoryName; }
        public int getItemCount() { return itemCount; }
        public int getTotalUnits() { return totalUnits; }
        public BigDecimal getTotalValue() { return totalValue; }
        public int getLowStockCount() { return lowStockCount; }
    }

    public static class InventoryAnalytics {
        private int totalItems;
        private int totalUnits;
        private BigDecimal totalValuation = BigDecimal.ZERO;
        private int lowStockCount;
        private String topCategory = "N/A";
        private int topCategoryUnits = 0;
        private final Map<String, CategorySummary> categoryBreakdown = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        public int getTotalItems() { return totalItems; }
        public int getTotalUnits() { return totalUnits; }
        public BigDecimal getTotalValuation() { return totalValuation; }
        public int getLowStockCount() { return lowStockCount; }
        public String getTopCategory() { return topCategory; }
        public int getTopCategoryUnits() { return topCategoryUnits; }
        public Map<String, CategorySummary> getCategoryBreakdown() { return categoryBreakdown; }
    }

    public InventoryAnalytics generateAnalytics() {
        InventoryAnalytics analytics = new InventoryAnalytics();
        List<Item> items = DAOFactory.getItemDAO().findAll();

        analytics.totalItems = items.size();

        for (Item item : items) {
            analytics.totalUnits += item.getQuantity();
            analytics.totalValuation = analytics.totalValuation.add(item.getTotalValue());
            if (item.isLowStock()) {
                analytics.lowStockCount++;
            }

            String catName = item.getCategoryName() != null ? item.getCategoryName() : "General";
            CategorySummary summary = analytics.categoryBreakdown.computeIfAbsent(catName, CategorySummary::new);
            summary.addItem(item);
        }

        // Determine top category
        for (CategorySummary summary : analytics.categoryBreakdown.values()) {
            if (summary.getTotalUnits() > analytics.topCategoryUnits) {
                analytics.topCategoryUnits = summary.getTotalUnits();
                analytics.topCategory = summary.getCategoryName();
            }
        }

        return analytics;
    }

    public String buildReportText() {
        InventoryAnalytics a = generateAnalytics();
        StringBuilder sb = new StringBuilder();
        String dateStr = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new Date());

        sb.append("========================================================================================\n");
        sb.append("                       SMART INVENTORY MANAGEMENT SYSTEM - REPORT                      \n");
        sb.append("                     Global Institute of Technology & Management                        \n");
        sb.append("              Summer Internship Partner: Cypher Analytica, Indore                       \n");
        sb.append("                             Generated: ").append(dateStr).append("\n");
        sb.append("========================================================================================\n\n");

        sb.append("--- EXECUTIVE SUMMARY ---\n");
        sb.append(String.format("  Total Unique Products : %,d\n", a.getTotalItems()));
        sb.append(String.format("  Total Stock Quantity  : %,d Units\n", a.getTotalUnits()));
        sb.append(String.format("  Active Categories     : %,d\n", a.getCategoryBreakdown().size()));
        sb.append(String.format("  Top Category by Volume: %s (%,d Units)\n", a.getTopCategory(), a.getTopCategoryUnits()));
        sb.append(String.format("  Low Stock Alert Items : %d\n", a.getLowStockCount()));
        sb.append(String.format("  Total Inventory Value : ₹%,.2f\n\n", a.getTotalValuation()));

        sb.append("--- STOCK LEVELS BY CATEGORY ---\n");
        sb.append(String.format("%-18s | %-10s | %-12s | %-16s | %-10s\n", 
                "Category", "Items Count", "Total Units", "Valuation (₹)", "Low Stock"));
        sb.append("----------------------------------------------------------------------------------------\n");
        for (CategorySummary cs : a.getCategoryBreakdown().values()) {
            sb.append(String.format("%-18s | %-10d | %-12d | ₹%-15.2f | %-10d\n",
                    cs.getCategoryName(), cs.getItemCount(), cs.getTotalUnits(), cs.getTotalValue(), cs.getLowStockCount()));
        }
        sb.append("----------------------------------------------------------------------------------------\n\n");

        List<Item> lowStockItems = DAOFactory.getItemDAO().findLowStockItems();
        sb.append("--- LOW STOCK ALERTS (ACTION REQUIRED) ---\n");
        if (lowStockItems.isEmpty()) {
            sb.append("  All items are sufficiently stocked above reorder thresholds.\n");
        } else {
            sb.append(String.format("%-8s | %-24s | %-8s | %-10s | %-12s | %-20s\n",
                    "SKU", "Item Name", "Current", "Threshold", "Status", "Supplier"));
            sb.append("----------------------------------------------------------------------------------------\n");
            for (Item item : lowStockItems) {
                sb.append(String.format("%-8s | %-24s | %-8d | %-10d | %-12s | %-20s\n",
                        item.getSku(),
                        item.getName().length() > 24 ? item.getName().substring(0, 21) + "..." : item.getName(),
                        item.getQuantity(),
                        item.getReorderThreshold(),
                        item.getStatusBadge(),
                        item.getSupplierName() != null ? item.getSupplierName() : "N/A"));
            }
        }
        sb.append("\n========================================================================================\n");
        return sb.toString();
    }

    public boolean exportReportToFile(String filename) {
        String report = buildReportText();
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.write(report);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving report to file: " + e.getMessage());
            return false;
        }
    }
}
