package com.cypheranalytica.inventory.ui;

import com.cypheranalytica.inventory.dao.DAOFactory;
import com.cypheranalytica.inventory.exception.DuplicateItemException;
import com.cypheranalytica.inventory.exception.InsufficientStockException;
import com.cypheranalytica.inventory.exception.InventoryException;
import com.cypheranalytica.inventory.exception.ItemNotFoundException;
import com.cypheranalytica.inventory.model.Category;
import com.cypheranalytica.inventory.model.Item;
import com.cypheranalytica.inventory.model.Supplier;
import com.cypheranalytica.inventory.model.Transaction;
import com.cypheranalytica.inventory.service.ConcurrencySimulationService;
import com.cypheranalytica.inventory.service.InventoryService;
import com.cypheranalytica.inventory.service.ReportService;
import com.cypheranalytica.inventory.service.SupplierService;
import com.cypheranalytica.inventory.service.TransactionService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Console User Interface.
 * Implements the menu structure shown in the internship presentation (Slide 14).
 * 
 * Part of Smart Inventory Management System
 * Developed by: Yash
 * Cypher Analytica Internship
 */
public class ConsoleMenu {

    private final InventoryService inventoryService;
    private final SupplierService supplierService;
    private final TransactionService transactionService;
    private final ReportService reportService;
    private final ConcurrencySimulationService simulationService;
    private final Scanner scanner;

    public ConsoleMenu() {
        this.inventoryService = new InventoryService();
        this.supplierService = new SupplierService();
        this.transactionService = new TransactionService();
        this.reportService = new ReportService();
        this.simulationService = new ConcurrencySimulationService(inventoryService, transactionService);
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = readIntChoice("Enter your choice: ", 0, 9);
            switch (choice) {
                case 1:
                    handleAddNewItem();
                    break;
                case 2:
                    handleUpdateStockQuantity();
                    break;
                case 3:
                    handleSearchItem();
                    break;
                case 4:
                    handleViewLowStockAlerts();
                    break;
                case 5:
                    handleGenerateInventoryReport();
                    break;
                case 6:
                    handleManageSuppliers();
                    break;
                case 7:
                    handleViewTransactionHistory();
                    break;
                case 8:
                    handleConcurrencySimulation();
                    break;
                case 9:
                    handleAboutProject();
                    break;
                case 0:
                    running = false;
                    displayExitMessage();
                    break;
                default:
                    System.out.println("Invalid selection. Please choose an option from the menu.");
            }

            if (running) {
                pause();
            }
        }
    }

    private void displayMainMenu() {
        System.out.println();
        System.out.println("================================================================================");
        System.out.println("                     SMART INVENTORY MANAGEMENT SYSTEM                          ");
        System.out.println("                  Cypher Analytica Summer Internship Project                    ");
        System.out.println("             Submitted by: Yash | Global Institute of Technology & Management       ");
        System.out.println("                     [Engine: In-Memory Collections]                            ");
        System.out.println("================================================================================");
        System.out.println("  1. Add New Item");
        System.out.println("  2. Update Stock Quantity (Stock In / Stock Out)");
        System.out.println("  3. Search Item by SKU / Name / Category");
        System.out.println("  4. View Low-Stock Alerts");
        System.out.println("  5. Generate Inventory Report");
        System.out.println("  6. Manage Suppliers");
        System.out.println("  7. View Transaction History");
        System.out.println("  8. Multithreading Concurrency Simulation (Stress Test)");
        System.out.println("  9. Project & Internship Information");
        System.out.println("  0. Exit");
        System.out.println("--------------------------------------------------------------------------------");
    }

    // -------------------------------------------------------------------------
    // OPTION 1: Add New Item
    // -------------------------------------------------------------------------
    private void handleAddNewItem() {
        System.out.println("\n----------------- [1. ADD NEW INVENTORY ITEM] -----------------");
        System.out.print("Enter Item SKU (e.g. 1042, 5020): ");
        String sku = scanner.nextLine().trim();

        System.out.print("Enter Item Name: ");
        String name = scanner.nextLine().trim();

        System.out.println("\nAvailable Categories:");
        List<Category> categories = inventoryService.getAllCategories();
        for (Category c : categories) {
            System.out.printf("  [%d] %s (%s)\n", c.getCategoryId(), c.getName(), c.getDescription());
        }
        int categoryId = readIntChoice("Select Category ID: ", 1, 9999);

        int initialQty = readInt("Enter Initial Quantity in Stock: ", 0, 1000000);
        BigDecimal price = readBigDecimal("Enter Unit Price (₹): ");
        int threshold = readInt("Enter Low-Stock Reorder Threshold (e.g. 10): ", 1, 10000);

        System.out.println("\nAvailable Suppliers:");
        List<Supplier> suppliers = supplierService.getActiveSuppliers();
        for (Supplier s : suppliers) {
            System.out.printf("  [%d] %s (%s)\n", s.getSupplierId(), s.getName(), s.getPhone());
        }
        System.out.print("Enter Supplier ID (or 0 for None): ");
        int supChoice = readIntChoice("", 0, 9999);
        Integer supplierId = (supChoice > 0) ? supChoice : null;

        try {
            Item created = inventoryService.addItem(sku, name, categoryId, initialQty, price, threshold, supplierId);
            System.out.println("\n>> [SUCCESS] Item successfully added to inventory!");
            System.out.println("   ID       : " + created.getItemId());
            System.out.println("   SKU      : " + created.getSku());
            System.out.println("   Name     : " + created.getName());
            System.out.println("   Quantity : " + created.getQuantity() + " units");
            System.out.println("   Price    : ₹" + created.getUnitPrice());
            System.out.println("   Status   : " + created.getStatusBadge());
        } catch (DuplicateItemException e) {
            System.err.println(">> [ERROR] Item creation failed: " + e.getMessage());
        } catch (InventoryException e) {
            System.err.println(">> [ERROR] Validation failure: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // OPTION 2: Update Stock Quantity (Stock In / Stock Out)
    // -------------------------------------------------------------------------
    private void handleUpdateStockQuantity() {
        System.out.println("\n----------- [2. UPDATE STOCK QUANTITY (TRANSACTIONS)] -----------");
        System.out.println("  1. Stock-In (Procurement / Restock received)");
        System.out.println("  2. Stock-Out (Sales / Dispatch / Consumption)");
        System.out.println("  0. Back to Main Menu");
        int sub = readIntChoice("Choose operation: ", 0, 2);
        if (sub == 0) return;

        System.out.print("Enter Item SKU or Item ID: ");
        String identifier = scanner.nextLine().trim();

        Item item = null;
        try {
            if (identifier.matches("\\d+")) {
                int id = Integer.parseInt(identifier);
                try {
                    item = inventoryService.getItemById(id);
                } catch (ItemNotFoundException ignored) {
                }
            }
            if (item == null) {
                item = inventoryService.getItemBySku(identifier);
            }
        } catch (ItemNotFoundException e) {
            System.err.println(">> [NOT FOUND] " + e.getMessage());
            return;
        }

        System.out.printf("\nSelected Item: [%s] %s | Current Available: %d units | Price: ₹%.2f\n",
                item.getSku(), item.getName(), item.getQuantity(), item.getUnitPrice());

        if (sub == 1) { // Stock-In
            int qty = readInt("Enter Quantity to Add (Stock-In): ", 1, 100000);
            System.out.print("Enter Receiving Counter / Location (default: Warehouse): ");
            String counter = scanner.nextLine().trim();
            if (counter.isEmpty()) counter = "Warehouse Receiving";

            System.out.print("Enter Remarks / Supplier Invoice No: ");
            String remarks = scanner.nextLine().trim();
            if (remarks.isEmpty()) remarks = "Regular Restock";

            try {
                Transaction txn = transactionService.recordStockIn(item.getItemId(), qty, counter, remarks);
                System.out.println("\n>> [SUCCESS] Stock Inward Recorded!");
                System.out.printf("   Transaction Code : %s\n", txn.getTxnCode());
                System.out.printf("   Units Added      : +%d\n", qty);
                System.out.printf("   Updated Stock    : %d units\n", item.getQuantity() + qty);
                System.out.printf("   Total Value      : ₹%.2f\n", txn.getTotalAmount());
            } catch (InventoryException e) {
                System.err.println(">> [ERROR] " + e.getMessage());
            }

        } else { // Stock-Out
            int qty = readInt("Enter Quantity to Deduct (Stock-Out / Sale): ", 1, 100000);
            System.out.print("Enter Billing Counter / Cashier (default: Billing Counter 1): ");
            String counter = scanner.nextLine().trim();
            if (counter.isEmpty()) counter = "Billing Counter 1";

            System.out.print("Enter Remarks / Customer Order ID: ");
            String remarks = scanner.nextLine().trim();
            if (remarks.isEmpty()) remarks = "Retail Sale";

            try {
                Transaction txn = transactionService.recordStockOut(item.getItemId(), qty, counter, remarks);
                System.out.println("\n>> [SUCCESS] Stock Outward Recorded!");
                System.out.printf("   Transaction Code : %s\n", txn.getTxnCode());
                System.out.printf("   Units Deducted   : -%d\n", qty);
                System.out.printf("   Remaining Stock  : %d units\n", item.getQuantity() - qty);
                System.out.printf("   Total Billed     : ₹%.2f\n", txn.getTotalAmount());

                // Low stock warning if threshold reached
                if ((item.getQuantity() - qty) <= item.getReorderThreshold()) {
                    System.out.println("   ⚠️ WARNING: Item has dropped below its reorder threshold (" + item.getReorderThreshold() + ")!");
                }
            } catch (InsufficientStockException e) {
                System.err.println("\n>> [TRANSACTION REJECTED] " + e.getMessage());
                System.err.println("   Transaction cancelled: cannot oversell stock.");
            } catch (InventoryException e) {
                System.err.println(">> [ERROR] " + e.getMessage());
            }
        }
    }

    // -------------------------------------------------------------------------
    // OPTION 3: Search Item by ID / Name / Category
    // -------------------------------------------------------------------------
    private void handleSearchItem() {
        System.out.println("\n----------------- [3. SEARCH ITEM INVENTORY] ------------------");
        System.out.print("Enter SKU Code or Product Name Keyword (or press Enter to list all): ");
        String query = scanner.nextLine().trim();

        List<Item> items = inventoryService.searchItems(query);
        if (items.isEmpty()) {
            System.out.println(">> No items found matching '" + query + "'.");
            return;
        }

        printItemsTable(items);
    }

    // -------------------------------------------------------------------------
    // OPTION 4: View Low-Stock Alerts (Matching Slide 14)
    // -------------------------------------------------------------------------
    private void handleViewLowStockAlerts() {
        System.out.println("\n------------------ [4. VIEW LOW-STOCK ALERTS] ------------------");
        List<Item> lowStockList = inventoryService.getLowStockAlerts();

        System.out.println(">> Low Stock Alert: " + lowStockList.size() + " items found");
        if (lowStockList.isEmpty()) {
            System.out.println("   All products have healthy inventory levels above reorder threshold.");
            return;
        }

        for (Item item : lowStockList) {
            System.out.printf("   [%s] %-22s Qty: %-4d (Min: %-3d) | Status: %s | Supplier: %s\n",
                    item.getSku(),
                    item.getName(),
                    item.getQuantity(),
                    item.getReorderThreshold(),
                    item.getStatusBadge(),
                    item.getSupplierName() != null ? item.getSupplierName() : "N/A");
        }
    }

    // -------------------------------------------------------------------------
    // OPTION 5: Generate Inventory Report (Matching Slide 14 & 15)
    // -------------------------------------------------------------------------
    private void handleGenerateInventoryReport() {
        System.out.println("\n---------------- [5. INVENTORY ANALYTICS & REPORT] --------------");
        ReportService.InventoryAnalytics a = reportService.generateAnalytics();

        System.out.println("Sample Report — Stock Levels by Category");
        System.out.println("-----------------------------------------------------------------");
        System.out.printf("  Total Units   : %,d\n", a.getTotalUnits());
        System.out.printf("  Categories    : %d\n", a.getCategoryBreakdown().size());
        System.out.printf("  Top Category  : %s (%d Units)\n", a.getTopCategory(), a.getTopCategoryUnits());
        System.out.printf("  Total Value   : ₹%,.2f\n", a.getTotalValuation());
        System.out.printf("  Low-Stock     : %d items\n", a.getLowStockCount());
        System.out.println("-----------------------------------------------------------------");

        System.out.printf("%-18s | %-12s | %-14s | %-12s\n", "Category", "Item Types", "Total Units", "Valuation (₹)");
        System.out.println("-----------------------------------------------------------------");
        for (ReportService.CategorySummary cs : a.getCategoryBreakdown().values()) {
            System.out.printf("%-18s | %-12d | %-14d | ₹%-12.2f\n",
                    cs.getCategoryName(), cs.getItemCount(), cs.getTotalUnits(), cs.getTotalValue());
        }
        System.out.println("-----------------------------------------------------------------");

        System.out.print("\nDo you want to export the complete report to 'inventory_report.txt'? (y/n): ");
        String ans = scanner.nextLine().trim();
        if (ans.equalsIgnoreCase("y") || ans.equalsIgnoreCase("yes")) {
            boolean ok = reportService.exportReportToFile("inventory_report.txt");
            if (ok) {
                System.out.println(">> Report successfully saved to 'inventory_report.txt'.");
            }
        }
    }

    // -------------------------------------------------------------------------
    // OPTION 6: Manage Suppliers
    // -------------------------------------------------------------------------
    private void handleManageSuppliers() {
        System.out.println("\n------------------- [6. SUPPLIER MANAGEMENT] -------------------");
        System.out.println("  1. List All Active Suppliers");
        System.out.println("  2. Add New Supplier");
        System.out.println("  0. Back to Main Menu");
        int sub = readIntChoice("Choose option: ", 0, 2);

        if (sub == 1) {
            List<Supplier> suppliers = supplierService.getActiveSuppliers();
            System.out.println("\n--------------------------------------------------------------------------------");
            System.out.printf("%-4s | %-20s | %-16s | %-15s | %-20s\n", "ID", "Company Name", "Contact Person", "Phone", "Email");
            System.out.println("--------------------------------------------------------------------------------");
            for (Supplier s : suppliers) {
                System.out.printf("%-4d | %-20s | %-16s | %-15s | %-20s\n",
                        s.getSupplierId(), s.getName(), s.getContactPerson(), s.getPhone(), s.getEmail());
            }
            System.out.println("--------------------------------------------------------------------------------");
        } else if (sub == 2) {
            System.out.print("Enter Supplier Company Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Contact Person Name: ");
            String contact = scanner.nextLine().trim();
            System.out.print("Enter Contact Phone: ");
            String phone = scanner.nextLine().trim();
            System.out.print("Enter Email Address: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter Office / Warehouse Address: ");
            String address = scanner.nextLine().trim();

            try {
                Supplier s = supplierService.addSupplier(name, contact, phone, email, address);
                System.out.println(">> [SUCCESS] Supplier added with ID: " + s.getSupplierId());
            } catch (InventoryException e) {
                System.err.println(">> [ERROR] " + e.getMessage());
            }
        }
    }

    // -------------------------------------------------------------------------
    // OPTION 7: View Transaction History
    // -------------------------------------------------------------------------
    private void handleViewTransactionHistory() {
        System.out.println("\n----------------- [7. TRANSACTION HISTORY & AUDIT] ----------------");
        List<Transaction> txns = transactionService.getRecentTransactions(15);
        if (txns.isEmpty()) {
            System.out.println(">> No transactions recorded yet.");
            return;
        }

        System.out.println("Showing recent " + txns.size() + " stock transactions:");
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.printf("%-14s | %-9s | %-22s | %-5s | %-11s | %-18s | %-18s\n",
                "Txn Code", "Type", "Item Name", "Qty", "Amount (₹)", "Counter", "Remarks");
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (Transaction t : txns) {
            String shortName = t.getItemName();
            if (shortName != null && shortName.length() > 22) {
                shortName = shortName.substring(0, 19) + "...";
            }
            System.out.printf("%-14s | %-9s | %-22s | %-5d | ₹%-10.2f | %-18s | %-18s\n",
                    t.getTxnCode(),
                    t.getTxnType(),
                    shortName != null ? shortName : "N/A",
                    t.getQuantity(),
                    t.getTotalAmount(),
                    t.getCounterName(),
                    t.getRemarks());
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
    }

    // -------------------------------------------------------------------------
    // OPTION 8: Multithreading Concurrency Simulation (Stress Test)
    // -------------------------------------------------------------------------
    private void handleConcurrencySimulation() {
        System.out.println("\n------------- [8. MULTITHREADING CONCURRENCY SIMULATION] -------------");
        System.out.println("This module demonstrates Java Multithreading (Thread, Runnable, synchronized)");
        System.out.println("by launching multiple worker threads attempting simultaneous stock checkout.");
        System.out.println("It proves that race conditions are prevented and overselling is impossible.\n");

        List<Item> items = inventoryService.getAllItems();
        System.out.println("Select an item for simulation:");
        for (int i = 0; i < Math.min(items.size(), 5); i++) {
            Item item = items.get(i);
            System.out.printf("  [%d] %-22s | Current Stock: %d units (SKU: %s)\n",
                    item.getItemId(), item.getName(), item.getQuantity(), item.getSku());
        }

        int itemId = readIntChoice("Enter Item ID to test (e.g. 1 or 2): ", 1, 9999);
        int threads = readIntChoice("Enter number of concurrent counter threads (e.g. 4): ", 2, 20);
        int unitsEach = readIntChoice("Enter units requested per counter (e.g. 2): ", 1, 100);

        simulationService.runSimulation(itemId, threads, unitsEach);
    }

    // -------------------------------------------------------------------------
    // OPTION 9: Project & Internship Information
    // -------------------------------------------------------------------------
    private void handleAboutProject() {
        System.out.println("\n------------------ [9. PROJECT INFORMATION] -------------------");
        System.out.println("  Project Title   : Smart Inventory Management System");
        System.out.println("  Developer       : Yash");
        System.out.println("  Degree & Branch : B.Tech (AI & DS), 3rd Year");
        System.out.println("  College         : Global Institute of Technology & Management, Gurugram");
        System.out.println("  Internship      : Cypher Analytica, Indore (June - August 2026)");
        System.out.println("  Industry Mentor : Mr. Rajesh Kumar (Center Manager)");
        System.out.println("  Academic Guide  : Ms. Barkha Tanwar");
        System.out.println("  Engine          : In-Memory Java Collections (ArrayList, HashMap, TreeMap)");
        System.out.println("  Concurrency     : Multithreading (Runnable, synchronized blocks)");
        System.out.println("  Web UI          : http://localhost:8080 (Modern Dashboard)");
        System.out.println("----------------------------------------------------------------");
    }

    private void displayExitMessage() {
        System.out.println("\n================================================================================");
        System.out.println("       Thank you for using Smart Inventory Management System!                    ");
        System.out.println("             Project by: Yash                                                   ");
        System.out.println("   Global Institute of Technology & Management | Cypher Analytica               ");
        System.out.println("================================================================================\n");
    }

    // -------------------------------------------------------------------------
    // UI Helpers & Table Formatters
    // -------------------------------------------------------------------------
    private void printItemsTable(List<Item> items) {
        System.out.println("\n-------------------------------------------------------------------------------------------------");
        System.out.printf("%-4s | %-8s | %-24s | %-14s | %-5s | %-10s | %-5s | %-12s\n",
                "ID", "SKU", "Item Name", "Category", "Qty", "Price (₹)", "Min", "Status");
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (Item item : items) {
            String shortName = item.getName();
            if (shortName.length() > 24) {
                shortName = shortName.substring(0, 21) + "...";
            }
            String catName = item.getCategoryName() != null ? item.getCategoryName() : "General";
            if (catName.length() > 14) {
                catName = catName.substring(0, 11) + "...";
            }

            System.out.printf("%-4d | %-8s | %-24s | %-14s | %-5d | ₹%-9.2f | %-5d | %-12s\n",
                    item.getItemId(),
                    item.getSku(),
                    shortName,
                    catName,
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getReorderThreshold(),
                    item.getStatusBadge());
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.printf("Total items displayed: %d\n", items.size());
    }

    private int readIntChoice(String prompt, int min, int max) {
        while (true) {
            if (!prompt.isEmpty()) {
                System.out.print(prompt);
            }
            if (!scanner.hasNextLine()) {
                return 0; // Return exit on EOF
            }
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf("Please enter a valid number between %d and %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.print("Invalid numeric input. Please try again: ");
            }
        }
    }

    private int readInt(String prompt, int min, int max) {
        return readIntChoice(prompt, min, max);
    }

    private BigDecimal readBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                return BigDecimal.ZERO;
            }
            String line = scanner.nextLine().trim();
            try {
                BigDecimal val = new BigDecimal(line);
                if (val.compareTo(BigDecimal.ZERO) >= 0) {
                    return val;
                }
                System.out.println("Price cannot be negative.");
            } catch (Exception e) {
                System.out.println("Invalid currency amount. Example format: 149.00");
            }
        }
    }

    private void pause() {
        System.out.print("\nPress [ENTER] to return to menu...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }
}
