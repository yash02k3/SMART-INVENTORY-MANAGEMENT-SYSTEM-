# Smart Inventory Management System using Core Java & Web Dashboard 📦🚀

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Architecture](https://img.shields.io/badge/Architecture-Web%20Dashboard%20%2B%20Core%20Java-green?style=for-the-badge)]()
[![Internship](https://img.shields.io/badge/Internship-Cypher%20Analytica-blue?style=for-the-badge)]()
[![Status](https://img.shields.io/badge/Zero--Config-In--Memory%20Demo%20Ready-brightgreen?style=for-the-badge)]()

A complete **Smart Inventory Management System** featuring a **modern responsive Web Dashboard** (matching Slides 15 & 16 of the project report) backed by **Core Java, Multithreading, and Collections**. Pre-populated with realistic dummy data — **requires NO external database setup**!

---

## 🎓 Academic & Internship Details

| Attribute | Details |
| :--- | :--- |
| **Student Name** | **Payal** |
| **Roll Number** | **248036** |
| **Registration No.** | **241394000031** |
| **Degree & Branch** | **B.Tech in Artificial Intelligence & Data Science (AI & DS), 3rd Year** |
| **Institution** | **Global Institute of Technology & Management (GITM), Gurugram** |
| **Internship Partner** | **Cypher Analytica, Indore (M.P.)** |
| **Duration** | **02 June 2026 – 02 August 2026** |
| **Industry Mentor** | **Mr. Rajesh Kumar (Center Manager, Cypher Analytica)** |
| **Academic Guide** | **Ms. Barkha Tanwar (GITM, Gurugram)** |

---

## 🌟 Key Highlights & Features

1. **Modern Web Dashboard (Matching Report Slide 15)**:
   - **KPI Summary Cards**: Total Stock Units (1,248 units), Low-Stock Alerts (4 critical), Active Suppliers (5 vendors), Total Inventory Valuation (₹5,63,578).
   - **Stock Levels by Category**: Visual horizontal bar charts breaking down units across Electronics, Hardware, Stationery, Packaging, and Furniture.
   - **Low-Stock Alerts Feed**: Ranked list of critical items with quick "+ Restock" buttons.
   - **Recent Transactions Log**: Latest stock inward/outward movements with color-coded status badges.

2. **Item Inventory Catalog (Matching Report Slide 16)**:
   - Real-time search by Product Name or SKU.
   - Filter pills: "All Items", "Low Stock", category filter, supplier filter.
   - Action buttons: Quick Stock Inward (+In), Stock Outward (-Out with oversell validation), and Delete.
   - "+ Add New Item" interactive modal.

3. **Multithreading Concurrency Simulator (Week 4 Topic & Slides 12, 18, 19)**:
   - Live browser & terminal simulation testing simultaneous checkout orders across multiple cashier counters.
   - Demonstrates Java `synchronized` blocks preventing race conditions and **proving 0 overselling**.

4. **Zero Database Setup (Instant Out-of-the-Box Run)**:
   - Built-in lightweight Java HTTP server (`com.sun.net.httpserver`) and in-memory Collections engine.
   - No MySQL, no Node.js, and no external installation required.

---

## ⚡ Quick Start: How to Run

### Option A: The Fastest Way (1-Click Run)

* **Windows Users**:
  Double-click **`run.bat`** (or open Command Prompt and type `run.bat`).
  *It compiles Java, starts the web server, and opens `http://localhost:8080` in your browser!*

* **macOS / Linux Users**:
  Open Terminal in the folder and run:
  ```bash
  ./run.sh
  ```
  *(Then open `http://localhost:8080` in your browser).*

* **Direct Browser View (Zero Java needed!)**:
  Just double click **`web/index.html`** in any browser (Chrome, Edge, Safari). It works 100% standalone with the pre-loaded dataset!

---

### Option B: Running from Terminal (Step-by-Step)

#### On Windows (Command Prompt):
```cmd
mkdir bin
dir /s /b src\*.java > sources.txt
javac -cp "lib/*;src" -d bin @sources.txt
del sources.txt
java -cp "bin;lib/*" com.cypheranalytica.inventory.Main
```

#### On macOS / Linux:
```bash
javac -cp "lib/*:src" -d bin $(find src -name "*.java")
java -cp "bin:lib/*" com.cypheranalytica.inventory.Main
```

---

## 👥 How to Run on Any Windows PC from Scratch (For Evaluators & Friends)

If the person testing the project has a fresh Windows laptop with nothing installed:

### Step 1: Install Java (Takes 1 minute)
Open Windows Terminal or PowerShell and run:
```powershell
winget install EclipseAdoptium.Temurin.17.JDK
```
*(Or download standard Java JDK from [Adoptium.net](https://adoptium.net/)).*

### Step 2: Download the Project
```cmd
git clone https://github.com/yash02k3/SMART-INVENTORY-MANAGEMENT-SYSTEM-.git
cd SMART-INVENTORY-MANAGEMENT-SYSTEM-
```
*(Or click "Code -> Download ZIP" on GitHub and extract the folder).*

### Step 3: Run
Double click **`run.bat`**! The dashboard will launch at `http://localhost:8080`.

---

## 📁 Project Architecture & File Structure

```text
SMART-INVENTORY-MANAGEMENT-SYSTEM-/
├── web/                                  # Modern Web Dashboard (Slides 15 & 16)
│   ├── index.html                        # Main frontend dashboard
│   ├── styles.css                        # Modern CSS theme & responsive layout
│   └── app.js                            # Frontend controller & simulation logic
├── src/
│   ├── Main.java                         # Entry point launcher
│   └── com/cypheranalytica/inventory/
│       ├── Main.java                     # Application bootstrap & banner
│       ├── WebServer.java                # Embedded zero-dependency HTTP server
│       ├── model/                        # Domain entities (Item, Supplier, Txn)
│       ├── dao/                          # In-memory & JDBC persistence layer
│       ├── service/                      # Business logic & simulation service
│       ├── exception/                    # Custom exception hierarchy
│       └── ui/                           # Interactive Console CLI UI
├── config/
│   └── db.properties                     # Database config (MySQL optional)
├── lib/
│   └── mysql-connector-j-8.4.0.jar       # Bundled MySQL JDBC Driver
├── sql/
│   └── schema.sql                        # Optional MySQL schema & seed data
├── pom.xml                               # Maven configuration
├── run.bat                               # Windows 1-click runner
├── run.sh                                # macOS/Linux 1-click runner
├── HOW_TO_RUN.md                         # Easy beginner setup guide
└── README.md                             # Documentation
```

---

## 📜 Acknowledgements & Certificate Reference

- **Training Organization**: Cypher Analytica, Indore (M.P.)
- **College**: Global Institute of Technology & Management, Gurugram (Affiliated to M.D.U. Rohtak)
- **Author**: Payal | B.Tech AI & DS 3rd Year (Roll No: 248036)
- **Industry Mentor**: Mr. Rajesh Kumar (Center Manager)
- **Academic Guide**: Ms. Barkha Tanwar
