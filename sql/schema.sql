-- ====================================================================
-- SMART INVENTORY MANAGEMENT SYSTEM
-- Developed by: Yash
-- Global Institute of Technology & Management (GITM), Gurugram
-- Internship Partner: Cypher Analytica, Indore
-- Database: MySQL 8.0+
-- ====================================================================

-- 1. Create Database if not exists
CREATE DATABASE IF NOT EXISTS inventory_db;
USE inventory_db;

-- 2. Drop existing tables if needed (in reverse order of foreign keys)
DROP TABLE IF EXISTS stock_audit_log;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS categories;

-- ====================================================================
-- TABLE 1: categories
-- ====================================================================
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- TABLE 2: suppliers
-- ====================================================================
CREATE TABLE suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    address TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- TABLE 3: items (Products / Inventory Catalog)
-- ====================================================================
CREATE TABLE items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    category_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    unit_price DECIMAL(10, 2) NOT NULL,
    reorder_threshold INT NOT NULL DEFAULT 10,
    supplier_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_item_category FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE RESTRICT,
    CONSTRAINT fk_item_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE SET NULL,
    INDEX idx_items_sku (sku),
    INDEX idx_items_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- TABLE 4: transactions (Stock In / Stock Out log)
-- ====================================================================
CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    txn_code VARCHAR(50) NOT NULL UNIQUE,
    item_id INT NOT NULL,
    txn_type ENUM('STOCK_IN', 'STOCK_OUT') NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    counter_name VARCHAR(100) DEFAULT 'Main Counter',
    remarks VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_txn_item FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE,
    INDEX idx_txn_item_id (item_id),
    INDEX idx_txn_date (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- TABLE 5: stock_audit_log (Audit trail for quantity adjustments)
-- ====================================================================
CREATE TABLE stock_audit_log (
    audit_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT NOT NULL,
    old_quantity INT NOT NULL,
    new_quantity INT NOT NULL,
    change_type VARCHAR(50) NOT NULL,
    performed_by VARCHAR(100) DEFAULT 'System',
    notes VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_item FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- SEED INITIAL DATA (Matching Report & Presentation Samples)
-- ====================================================================

-- Categories
INSERT INTO categories (category_id, name, description) VALUES
(1, 'Electronics', 'Electronic gadgets, accessories, and peripherals'),
(2, 'Hardware', 'Hardware tools, fasteners, and structural cables'),
(3, 'Stationery', 'Office paperwork, pens, stationery supplies'),
(4, 'Packaging', 'Packing boxes, bubble wraps, tapes'),
(5, 'Furniture', 'Office chairs, tables, and ergonomic furniture');

-- Suppliers
INSERT INTO suppliers (supplier_id, name, contact_person, phone, email, address) VALUES
(1, 'Shreeji Traders', 'Mahesh Patel', '+91 9826012345', 'info@shreejitraders.com', '124 Jawahar Marg, Indore, MP'),
(2, 'TechSupply Co.', 'Ankit Sharma', '+91 9876543210', 'sales@techsupply.in', 'Plot 45 Electronic Complex, Indore, MP'),
(3, 'Om Stationers', 'Ramesh Gupta', '+91 9425098765', 'orders@omstationers.com', '12 M.G. Road, Indore, MP'),
(4, 'Wrapwell Ltd.', 'Sunita Verma', '+91 9811223344', 'contact@wrapwell.com', '88 Pithampur Industrial Area, MP'),
(5, 'Comfort Interiors', 'Rajeev Mehra', '+91 9922334455', 'support@comfortinteriors.com', 'Sector B Sanwer Road, Indore, MP');

-- Items (Sample data exactly reflecting Slide 14 & 16)
INSERT INTO items (sku, name, category_id, quantity, unit_price, reorder_threshold, supplier_id) VALUES
('1042', 'USB Cable Type-C', 1, 4, 149.00, 10, 1),
('1077', 'Wireless Mouse', 1, 2, 499.00, 15, 2),
('1093', 'HDMI Adapter', 1, 6, 299.00, 12, 2),
('2210', 'A4 Paper Ream', 3, 265, 250.00, 50, 3),
('2215', 'Gel Pen (Pack of 10)', 3, 412, 120.00, 100, 3),
('3301', 'Packing Box (Medium)', 4, 11, 45.00, 25, 4),
('3305', 'Bubble Wrap Roll', 4, 180, 620.00, 30, 4),
('4401', 'Office Chair (Mesh)', 5, 95, 3499.00, 10, 5);

-- Initial Transactions for Audit Trail
INSERT INTO transactions (txn_code, item_id, txn_type, quantity, unit_price, total_amount, counter_name, remarks) VALUES
('TXN-1001', 1, 'STOCK_IN', 20, 149.00, 2980.00, 'Warehouse Receiving', 'Initial procurement batch'),
('TXN-1002', 1, 'STOCK_OUT', 16, 149.00, 2384.00, 'Counter 1', 'Retail sales to walk-in customer'),
('TXN-1003', 2, 'STOCK_IN', 50, 499.00, 24950.00, 'Warehouse Receiving', 'Restock shipment received'),
('TXN-1004', 2, 'STOCK_OUT', 48, 499.00, 23952.00, 'Counter 2 (Online)', 'Fulfilled e-commerce orders'),
('TXN-1005', 4, 'STOCK_IN', 300, 250.00, 75000.00, 'Warehouse Receiving', 'Bulk monthly delivery from Om Stationers'),
('TXN-1006', 4, 'STOCK_OUT', 35, 250.00, 8750.00, 'Corporate Desk', 'Internal consumption and dispatch');

-- Audit logs
INSERT INTO stock_audit_log (item_id, old_quantity, new_quantity, change_type, performed_by, notes) VALUES
(1, 0, 20, 'RESTOCK', 'Admin', 'Initial stock intake'),
(1, 20, 4, 'SALE', 'Counter 1', 'Sold 16 units'),
(2, 0, 50, 'RESTOCK', 'Admin', 'Initial stock intake'),
(2, 50, 2, 'SALE', 'Counter 2', 'Sold 48 units');
