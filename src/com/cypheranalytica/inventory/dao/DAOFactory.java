package com.cypheranalytica.inventory.dao;

/**
 * Factory pattern class to manage Data Access Objects (DAO).
 * Allows seamless switching between MySQL persistence and In-Memory Demo mode.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Payal (Roll No. 248036)
 * Cypher Analytica Internship
 */
public class DAOFactory {

    public enum StorageMode {
        MYSQL,
        IN_MEMORY_DEMO
    }

    private static StorageMode currentMode = StorageMode.IN_MEMORY_DEMO;
    private static boolean initialized = false;

    // MySQL DAOs
    private static final CategoryDAO mysqlCategoryDAO = new MySQLCategoryDAO();
    private static final SupplierDAO mysqlSupplierDAO = new MySQLSupplierDAO();
    private static final ItemDAO mysqlItemDAO = new MySQLItemDAO();
    private static final TransactionDAO mysqlTransactionDAO = new MySQLTransactionDAO();
    private static final AuditDAO mysqlAuditDAO = new MySQLAuditDAO();

    // In-Memory DAOs (preloaded with sample data from internship presentation)
    private static final CategoryDAO inMemoryCategoryDAO = new InMemoryCategoryDAO();
    private static final SupplierDAO inMemorySupplierDAO = new InMemorySupplierDAO();
    private static final ItemDAO inMemoryItemDAO = new InMemoryItemDAO();
    private static final TransactionDAO inMemoryTransactionDAO = new InMemoryTransactionDAO();
    private static final AuditDAO inMemoryAuditDAO = new InMemoryAuditDAO();

    /**
     * Attempts to auto-detect and initialize database connection.
     * If MySQL is running and reachable, uses MySQL.
     * Otherwise, falls back gracefully to In-Memory Demo mode.
     */
    public static synchronized void initialize() {
        if (initialized) return;

        System.out.println(">> Checking database connection...");
        DBConnection db = DBConnection.getInstance();
        if (db.testConnection()) {
            db.ensureSchema();
            currentMode = StorageMode.MYSQL;
            System.out.println(">> [SUCCESS] Connected to MySQL Database (inventory_db).");
        } else {
            currentMode = StorageMode.IN_MEMORY_DEMO;
            System.out.println(">> [NOTICE] MySQL not detected or unreachable (" + db.getLastErrorMessage() + ").");
            System.out.println(">> [INFO] Switched to In-Memory Demo Mode with pre-populated project sample data.");
        }
        initialized = true;
    }

    public static StorageMode getCurrentMode() {
        return currentMode;
    }

    public static void setStorageMode(StorageMode mode) {
        currentMode = mode;
    }

    public static ItemDAO getItemDAO() {
        return (currentMode == StorageMode.MYSQL) ? mysqlItemDAO : inMemoryItemDAO;
    }

    public static SupplierDAO getSupplierDAO() {
        return (currentMode == StorageMode.MYSQL) ? mysqlSupplierDAO : inMemorySupplierDAO;
    }

    public static CategoryDAO getCategoryDAO() {
        return (currentMode == StorageMode.MYSQL) ? mysqlCategoryDAO : inMemoryCategoryDAO;
    }

    public static TransactionDAO getTransactionDAO() {
        return (currentMode == StorageMode.MYSQL) ? mysqlTransactionDAO : inMemoryTransactionDAO;
    }

    public static AuditDAO getAuditDAO() {
        return (currentMode == StorageMode.MYSQL) ? mysqlAuditDAO : inMemoryAuditDAO;
    }
}
