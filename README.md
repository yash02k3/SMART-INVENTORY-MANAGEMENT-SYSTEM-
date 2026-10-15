# Smart Inventory Management System using Core Java & Web Dashboard 📦🚀

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Architecture](https://img.shields.io/badge/Architecture-Web%20Dashboard%20%2B%20Core%20Java-green?style=for-the-badge)]()
[![Internship](https://img.shields.io/badge/Internship-Cypher%20Analytica-blue?style=for-the-badge)]()
[![Status](https://img.shields.io/badge/Zero--Config-In--Memory%20Demo%20Ready-brightgreen?style=for-the-badge)]()

A complete **Smart Inventory Management System** featuring a **modern responsive Web Dashboard** backed by **Core Java (OOP, Collections, Multithreading, and Built-in HTTP Server)**. Pre-populated with realistic dummy data — **requires NO external database setup**!

---

## 🎓 Academic & Project Information

| Attribute | Details |
| :--- | :--- |
| **Developer / Author** | **Yash** |
| **Degree & Branch** | **B.Tech (Computer Science & Engineering / AI & DS)** |
| **Institution** | **Global Institute of Technology & Management (GITM), Gurugram** |
| **Internship Partner** | **Cypher Analytica, Indore (M.P.)** |
| **Project Type** | **Core Java Academic Internship & Major Project** |
| **Industry Mentor** | **Mr. Rajesh Kumar (Center Manager, Cypher Analytica)** |
| **Academic Guide** | **Ms. Barkha Tanwar (GITM, Gurugram)** |

---

## 🔑 Login Credentials

When launching the Web Dashboard, sign in using either of the following demo credentials:

| Username | Password | Access Level |
| :--- | :--- | :--- |
| **`admin`** | **`admin123`** | Full Administrator Access |
| **`yash`** | **`admin123`** | Project Lead / Admin Access |

---

## 🌟 Key Features & Capabilities

1. **Modern Web Dashboard**:
   - **KPI Summary Cards**: Real-time Total Stock Units, Low-Stock Alerts, Active Suppliers, and Total Inventory Valuation.
   - **Stock Levels by Category**: Visual horizontal progress bars breaking down inventory across Electronics, Hardware, Stationery, Packaging, and Furniture.
   - **Low-Stock Alerts Feed**: Ranked list of critical items with 1-click **"+ Restock"** modal.
   - **Recent Transactions Log**: Chronological stock inward and outward movement log with color-coded badges.

2. **Item Inventory Catalog**:
   - Live search by Product Name or SKU code.
   - Quick filter tabs: *All Items*, *Low Stock*, *Category*, and *Supplier*.
   - Instant Stock Adjustment: **+In** (Stock Inward) and **-Out** (Stock Outward with insufficient stock validation).
   - Interactive **"+ Add New Item"** modal with automatic SKU generation.

3. **Supplier & Transaction Management**:
   - Manage vendor directories, contact information, and supplier performance records.
   - Audit trail tracking all stock movements with timestamp and transaction type.

4. **Zero Database Configuration (Instant Run)**:
   - Runs out-of-the-box using Java In-Memory Collections (`ConcurrentHashMap`, `ArrayList`, `TreeMap`).
   - Built-in lightweight Java HTTP server (`com.sun.net.httpserver.HttpServer`) — no Tomcat, no MySQL, no Node.js required!

---

## ⚡ Quick Start Guide (How to Run)

### 💻 On Windows (Easiest Way — 1-Click)
1. Open the project folder.
2. **Double-click `run.bat`** (or open Command Prompt and type `run.bat`).
3. It will automatically compile the Java code, start the embedded web server, and open `http://localhost:8080` in your default browser.

### 🍎 On macOS / Linux
Open your Terminal inside the project directory and run:
```bash
./run.sh
```
Then open `http://localhost:8080` in your web browser.

### 🌐 Standalone Browser Mode (Zero Java Required!)
If you just want to preview or demonstrate the UI without running Java:
- Double click **`web/index.html`** in any web browser (Chrome, Edge, Safari, Firefox).
- It runs 100% standalone with pre-loaded mock data!

---

## 🖥️ Running on a Fresh Windows PC from Scratch (For Evaluators & Friends)

If someone is testing this project on a brand new Windows machine with nothing installed, share these 3 simple steps:

### Step 1: Install Java (Takes 1 minute)
Open Windows Terminal or PowerShell as Administrator and run:
```powershell
winget install EclipseAdoptium.Temurin.17.JDK
```
*(Or download and install standard Java JDK 17+ from [Adoptium.net](https://adoptium.net/)).*

### Step 2: Download the Project
```cmd
git clone https://github.com/yash02k3/SMART-INVENTORY-MANAGEMENT-SYSTEM-.git
cd SMART-INVENTORY-MANAGEMENT-SYSTEM-
```
*(Or click **Code -> Download ZIP** on GitHub and extract the folder).*

### Step 3: Run the Application
Double-click **`run.bat`**! The dashboard will start and open automatically at `http://localhost:8080`.

---

## 📁 File-by-File Codebase Explanation (For Project Evaluation & Viva)

Use this reference to explain what each file does during your college project presentation or viva:

| File / Folder | Purpose & Explanation |
| :--- | :--- |
| **`web/index.html`** | **Frontend Single-Page Application (SPA)**: Contains the modern responsive HTML structure, CSS styling, and client-side JavaScript controller. Displays KPI cards, inventory tables, filter pills, restock modals, and login authentication. |
| **`web/app.js`** | **External Frontend Logic**: Standalone JavaScript controller with seed data, REST API synchronization, filtering, and event handlers. |
| **`web/styles.css`** | **External Stylesheet**: Modular CSS styling sheet for custom layout, dark sidebar, tables, cards, and animations. |
| **`src/com/cypheranalytica/inventory/Main.java`** | **Application Entry Point**: Bootstraps the application, displays the project banner in console, starts `WebServer`, and initializes `ConsoleMenu`. |
| **`src/com/cypheranalytica/inventory/WebServer.java`** | **Embedded HTTP Server**: Lightweight HTTP server using standard `com.sun.net.httpserver`. Serves web files and exposes REST API endpoints (`/api/items`, `/api/suppliers`, `/api/stats`, `/api/transactions`). |
| **`src/com/cypheranalytica/inventory/model/`** | **Data Entity Layer (Encapsulation)**: Holds plain Java entity classes (`Item.java`, `Supplier.java`, `Category.java`, `Transaction.java`, `StockAuditLog.java`) with private fields, getters, setters, and constructors. |
| **`src/com/cypheranalytica/inventory/dao/`** | **Data Access Object (DAO) Pattern**: Decouples business logic from data storage. Uses in-memory Java collections (`ConcurrentHashMap`, `ArrayList`, `TreeMap`) for thread-safe high-speed operations. |
| **`src/com/cypheranalytica/inventory/service/`** | **Business Logic Layer**: Enforces business validation rules (e.g., verifying stock availability before deduction, calculating total inventory valuation, generating reports). |
| **`src/com/cypheranalytica/inventory/ui/ConsoleMenu.java`** | **Interactive Terminal CLI UI**: Full terminal interface with formatted ASCII tables, allowing complete inventory management directly from the command line. |
| **`src/com/cypheranalytica/inventory/exception/`** | **Custom Exception Classes**: Domain-specific error handling (`InsufficientStockException`, `DuplicateItemException`, `ItemNotFoundException`, `InventoryException`). |
| **`run.bat`** | **Windows 1-Click Runner Script**: Batch script for Windows that verifies JDK, creates `bin/` directory, compiles all `.java` files, and runs the application. |
| **`run.sh`** | **macOS / Linux Runner Script**: Shell script with executable permissions for Unix systems to compile and run the project. |
| **`sql/schema.sql`** | **Database Schema (Optional)**: Relational schema script for MySQL (tables, foreign keys, sample seed data) for future database integration. |

---

## 📜 Acknowledgements

- **Institution**: Global Institute of Technology & Management (GITM), Gurugram (Affiliated to M.D.U. Rohtak)
- **Internship Organization**: Cypher Analytica, Indore (M.P.)
- **Project Lead**: Yash
- **Industry Mentor**: Mr. Rajesh Kumar (Center Manager)
- **Academic Guide**: Ms. Barkha Tanwar (GITM, Gurugram)
