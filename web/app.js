/**
 * InventoryPro - Smart Inventory Management System
 * Web Frontend Application Controller
 * Developed by: Payal (Roll No. 248036) | Cypher Analytica Internship
 */

// ====================================================================
// INITIAL SEED DATA (Matching Presentation & PDF Slides 14, 15, 16)
// ====================================================================
const SEED_CATEGORIES = [
    { id: 1, name: "Electronics", desc: "Electronic gadgets and peripherals" },
    { id: 2, name: "Hardware", desc: "Hardware tools, fasteners, and cables" },
    { id: 3, name: "Stationery", desc: "Office paperwork, pens, stationery" },
    { id: 4, name: "Packaging", desc: "Packing boxes, bubble wraps, tapes" },
    { id: 5, name: "Furniture", desc: "Office chairs and ergonomic furniture" }
];

const SEED_SUPPLIERS = [
    { id: 1, name: "Shreeji Traders", contact: "Mahesh Patel", phone: "+91 9826012345", email: "info@shreejitraders.com", address: "124 Jawahar Marg, Indore, MP" },
    { id: 2, name: "TechSupply Co.", contact: "Ankit Sharma", phone: "+91 9876543210", email: "sales@techsupply.in", address: "Plot 45 Electronic Complex, Indore, MP" },
    { id: 3, name: "Om Stationers", contact: "Ramesh Gupta", phone: "+91 9425098765", email: "orders@omstationers.com", address: "12 M.G. Road, Indore, MP" },
    { id: 4, name: "Wrapwell Ltd.", contact: "Sunita Verma", phone: "+91 9811223344", email: "contact@wrapwell.com", address: "88 Pithampur Industrial Area, MP" },
    { id: 5, name: "Comfort Interiors", contact: "Rajeev Mehra", phone: "+91 9922334455", email: "support@comfortinteriors.com", address: "Sector B Sanwer Road, Indore, MP" }
];

const SEED_ITEMS = [
    { id: 1, sku: "1042", name: "USB Cable Type-C", categoryId: 1, category: "Electronics", quantity: 4, unitPrice: 149.00, reorderThreshold: 10, supplierId: 1, supplier: "Shreeji Traders" },
    { id: 2, sku: "1077", name: "Wireless Mouse", categoryId: 1, category: "Electronics", quantity: 2, unitPrice: 499.00, reorderThreshold: 15, supplierId: 2, supplier: "TechSupply Co." },
    { id: 3, sku: "1093", name: "HDMI Adapter", categoryId: 2, category: "Hardware", quantity: 6, unitPrice: 299.00, reorderThreshold: 12, supplierId: 2, supplier: "TechSupply Co." },
    { id: 4, sku: "2210", name: "A4 Paper Ream", categoryId: 3, category: "Stationery", quantity: 265, unitPrice: 250.00, reorderThreshold: 50, supplierId: 3, supplier: "Om Stationers" },
    { id: 5, sku: "2215", name: "Gel Pen (Pack of 10)", categoryId: 3, category: "Stationery", quantity: 412, unitPrice: 120.00, reorderThreshold: 100, supplierId: 3, supplier: "Om Stationers" },
    { id: 6, sku: "3301", name: "Packing Box (Medium)", categoryId: 4, category: "Packaging", quantity: 11, unitPrice: 45.00, reorderThreshold: 25, supplierId: 4, supplier: "Wrapwell Ltd." },
    { id: 7, sku: "3305", name: "Bubble Wrap Roll", categoryId: 4, category: "Packaging", quantity: 180, unitPrice: 620.00, reorderThreshold: 30, supplierId: 4, supplier: "Wrapwell Ltd." },
    { id: 8, sku: "4401", name: "Office Chair (Mesh)", categoryId: 5, category: "Furniture", quantity: 95, unitPrice: 3499.00, reorderThreshold: 10, supplierId: 5, supplier: "Comfort Interiors" }
];

const SEED_TRANSACTIONS = [
    { id: 1, txnCode: "TXN-OUT-1006", itemId: 1, itemName: "USB Cable Type-C", sku: "1042", type: "STOCK_OUT", qty: 25, unitPrice: 149.00, total: 3725.00, counter: "Counter 1", remarks: "Retail store checkout", date: "18 Aug 2026, 14:30" },
    { id: 2, txnCode: "TXN-IN-1005", itemId: 2, itemName: "Wireless Mouse", sku: "1077", type: "STOCK_IN", qty: 60, unitPrice: 499.00, total: 29940.00, counter: "Warehouse Receiving", remarks: "Shipment from TechSupply", date: "18 Aug 2026, 11:15" },
    { id: 3, txnCode: "TXN-OUT-1004", itemId: 4, itemName: "A4 Paper Ream", sku: "2210", type: "STOCK_OUT", qty: 15, unitPrice: 250.00, total: 3750.00, counter: "Corporate Desk", remarks: "Internal dispatch", date: "17 Aug 2026, 16:45" },
    { id: 4, txnCode: "TXN-IN-1003", itemId: 7, itemName: "Bubble Wrap Roll", sku: "3305", type: "STOCK_IN", qty: 50, unitPrice: 620.00, total: 31000.00, counter: "Warehouse Receiving", remarks: "Procurement invoice #4812", date: "16 Aug 2026, 09:20" },
    { id: 5, txnCode: "TXN-OUT-1002", itemId: 2, itemName: "Wireless Mouse", sku: "1077", type: "STOCK_OUT", qty: 48, unitPrice: 499.00, total: 23952.00, counter: "Counter 2 (Online)", remarks: "E-commerce orders fulfillment", date: "15 Aug 2026, 17:00" },
    { id: 6, txnCode: "TXN-IN-1001", itemId: 1, itemName: "USB Cable Type-C", sku: "1042", type: "STOCK_IN", qty: 50, unitPrice: 149.00, total: 7450.00, counter: "Warehouse Receiving", remarks: "Initial shipment batch", date: "14 Aug 2026, 10:00" }
];

// ====================================================================
// APPLICATION STATE
// ====================================================================
class InventoryApp {
    constructor() {
        this.categories = [...SEED_CATEGORIES];
        this.suppliers = [...SEED_SUPPLIERS];
        this.items = [...SEED_ITEMS];
        this.transactions = [...SEED_TRANSACTIONS];

        this.currentFilter = "all";
        this.selectedCategory = "all";
        this.selectedSupplier = "all";
        this.searchQuery = "";
        this.txnFilter = "all";

        this.init();
    }

    async init() {
        this.setupAuth();
        this.setupNavigation();
        this.setupEventListeners();
        this.populateDropdowns();
        this.renderAll();

        // Attempt to sync with Java backend if available
        await this.syncWithBackend();
    }

    // ================================================================
    // AUTHENTICATION (LOGIN / LOGOUT)
    // ================================================================
    setupAuth() {
        const loginScreen = document.getElementById("login-screen");
        const mainApp = document.getElementById("main-app");
        const loginForm = document.getElementById("login-form");
        const loginError = document.getElementById("login-error");
        const btnLogout = document.getElementById("btn-logout");

        // Check if already logged in this session
        const isLoggedIn = sessionStorage.getItem("inventory_logged_in") === "true";
        if (isLoggedIn) {
            loginScreen.style.display = "none";
            mainApp.style.display = "flex";
        } else {
            loginScreen.style.display = "flex";
            mainApp.style.display = "none";
        }

        loginForm?.addEventListener("submit", (e) => {
            e.preventDefault();
            const username = document.getElementById("login-username").value.trim().toLowerCase();
            const password = document.getElementById("login-password").value.trim();

            // Accepted credentials: admin / admin123 OR payal / admin123
            if ((username === "admin" || username === "payal") && password === "admin123") {
                sessionStorage.setItem("inventory_logged_in", "true");
                loginScreen.style.display = "none";
                mainApp.style.display = "flex";
                loginError.style.display = "none";
                this.showToast("Welcome back! Signed in as " + (username === "payal" ? "Payal" : "Administrator"), "success");
            } else {
                loginError.textContent = "Invalid username or password! Check demo credentials below.";
                loginError.style.display = "block";
            }
        });

        btnLogout?.addEventListener("click", () => {
            sessionStorage.removeItem("inventory_logged_in");
            mainApp.style.display = "none";
            loginScreen.style.display = "flex";
            this.showToast("Signed out successfully.", "info");
        });
    }

    async syncWithBackend() {
        try {
            const res = await fetch("/api/items");
            if (res.ok) {
                const data = await res.json();
                if (Array.isArray(data) && data.length > 0) {
                    this.items = data;
                    this.renderAll();
                    console.log(">> Synced live data from Java Backend");
                }
            }
        } catch (e) {
            console.log(">> Running in standalone In-Memory Demo mode");
        }
    }

    // ================================================================
    // NAVIGATION
    // ================================================================
    setupNavigation() {
        const navLinks = document.querySelectorAll(".nav-item");
        navLinks.forEach(link => {
            link.addEventListener("click", (e) => {
                e.preventDefault();
                const viewName = link.getAttribute("data-view");
                this.switchView(viewName);
            });
        });

        // Quick buttons
        document.getElementById("btn-quick-alerts")?.addEventListener("click", () => {
            this.switchView("items");
            document.querySelector('.filter-pill[data-filter="low"]')?.click();
        });

        document.getElementById("btn-view-all-low")?.addEventListener("click", () => {
            this.switchView("items");
            document.querySelector('.filter-pill[data-filter="low"]')?.click();
        });

        document.getElementById("btn-goto-transactions")?.addEventListener("click", () => {
            this.switchView("transactions");
        });
    }

    switchView(viewName) {
        document.querySelectorAll(".nav-item").forEach(item => {
            item.classList.toggle("active", item.getAttribute("data-view") === viewName);
        });

        document.querySelectorAll(".content-view").forEach(view => {
            view.classList.toggle("active", view.id === `view-${viewName}`);
        });

        // Update headers
        const titles = {
            dashboard: { title: "Inventory Dashboard", sub: "Welcome back — here's what's happening in your store today." },
            items: { title: "Item Inventory", sub: "Manage, filter, and track all cataloged items in stock." },
            suppliers: { title: "Supplier Management", sub: "Maintain registered vendors, points of contact, and linked shipments." },
            transactions: { title: "Transactions Ledger", sub: "Complete historical log of stock inward and outward movements." },
            reports: { title: "Reports & Analytics", sub: "Comprehensive inventory metrics, valuation, and downloadable audit sheets." },
            about: { title: "Project & Internship Details", sub: "Academic details, mentor acknowledgements, and technical concepts." }
        };

        const heading = titles[viewName] || titles.dashboard;
        document.getElementById("page-heading").textContent = heading.title;
        document.getElementById("page-subheading").textContent = heading.sub;

        if (viewName === "reports") {
            this.renderAsciiReport();
        }
    }

    // ================================================================
    // EVENT LISTENERS
    // ================================================================
    setupEventListeners() {
        // Modal toggles
        document.getElementById("btn-open-add-modal")?.addEventListener("click", () => this.openModal("modal-add-item"));
        document.getElementById("btn-add-supplier-modal")?.addEventListener("click", () => this.openModal("modal-add-supplier"));

        document.querySelectorAll("[data-close-modal]").forEach(btn => {
            btn.addEventListener("click", () => this.closeAllModals());
        });

        document.querySelectorAll(".modal-overlay").forEach(overlay => {
            overlay.addEventListener("click", (e) => {
                if (e.target === overlay) this.closeAllModals();
            });
        });

        // Form submissions
        document.getElementById("form-add-item")?.addEventListener("submit", (e) => this.handleAddItem(e));
        document.getElementById("form-stock-action")?.addEventListener("submit", (e) => this.handleStockAction(e));
        document.getElementById("form-add-supplier")?.addEventListener("submit", (e) => this.handleAddSupplier(e));

        // Filters in Items View
        document.getElementById("items-search-input")?.addEventListener("input", (e) => {
            this.searchQuery = e.target.value.trim().toLowerCase();
            this.renderItemsTable();
        });

        document.querySelectorAll(".filter-pill[data-filter]").forEach(pill => {
            pill.addEventListener("click", () => {
                document.querySelectorAll(".filter-pill[data-filter]").forEach(p => p.classList.remove("active"));
                pill.classList.add("active");
                this.currentFilter = pill.getAttribute("data-filter");
                this.renderItemsTable();
            });
        });

        document.getElementById("category-filter-select")?.addEventListener("change", (e) => {
            this.selectedCategory = e.target.value;
            this.renderItemsTable();
        });

        document.getElementById("supplier-filter-select")?.addEventListener("change", (e) => {
            this.selectedSupplier = e.target.value;
            this.renderItemsTable();
        });

        // Txn filter
        document.querySelectorAll(".filter-pill[data-txn-filter]").forEach(pill => {
            pill.addEventListener("click", () => {
                document.querySelectorAll(".filter-pill[data-txn-filter]").forEach(p => p.classList.remove("active"));
                pill.classList.add("active");
                this.txnFilter = pill.getAttribute("data-txn-filter");
                this.renderTransactionsTable();
            });
        });

        // Download Report
        document.getElementById("btn-download-report")?.addEventListener("click", () => this.downloadReportFile());
    }

    // ================================================================
    // DROPDOWNS
    // ================================================================
    populateDropdowns() {
        const catSelect = document.getElementById("category-filter-select");
        if (catSelect) {
            catSelect.innerHTML = '<option value="all">All Categories</option>' +
                this.categories.map(c => `<option value="${c.name}">${c.name}</option>`).join("");
        }

        const supSelect = document.getElementById("supplier-filter-select");
        if (supSelect) {
            supSelect.innerHTML = '<option value="all">All Suppliers</option>' +
                this.suppliers.map(s => `<option value="${s.name}">${s.name}</option>`).join("");
        }

        const modalCat = document.getElementById("new-category");
        if (modalCat) {
            modalCat.innerHTML = this.categories.map(c => `<option value="${c.id}">${c.name}</option>`).join("");
        }

        const modalSup = document.getElementById("new-supplier");
        if (modalSup) {
            modalSup.innerHTML = this.suppliers.map(s => `<option value="${s.id}">${s.name}</option>`).join("");
        }
    }

    // ================================================================
    // RENDER FUNCTIONS
    // ================================================================
    renderAll() {
        this.renderKPIs();
        this.renderCategoryBars();
        this.renderLowStockFeed();
        this.renderRecentTransactions();
        this.renderItemsTable();
        this.renderSuppliersGrid();
        this.renderTransactionsTable();
        this.updateBadges();
    }

    updateBadges() {
        const lowStockCount = this.items.filter(i => i.quantity <= i.reorderThreshold).length;
        const totalItemsCount = this.items.length;

        const navAlertBadge = document.getElementById("nav-alert-badge");
        if (navAlertBadge) navAlertBadge.textContent = lowStockCount;

        const sidebarCount = document.getElementById("sidebar-item-count");
        if (sidebarCount) sidebarCount.textContent = totalItemsCount;
    }

    renderKPIs() {
        let totalUnits = 0;
        let totalValuation = 0;
        let lowStockCount = 0;

        this.items.forEach(item => {
            totalUnits += item.quantity;
            totalValuation += item.quantity * item.unitPrice;
            if (item.quantity <= item.reorderThreshold) {
                lowStockCount++;
            }
        });

        document.getElementById("kpi-total-units").textContent = totalUnits.toLocaleString();
        document.getElementById("kpi-low-stock").textContent = lowStockCount;
        document.getElementById("kpi-suppliers").textContent = this.suppliers.length;
        document.getElementById("kpi-total-value").textContent = `₹${totalValuation.toLocaleString('en-IN', { maximumFractionDigits: 2 })}`;

        const lowTag = document.getElementById("kpi-low-tag");
        if (lowTag) {
            lowTag.textContent = lowStockCount > 0 ? `${lowStockCount} critical` : "Optimal";
            lowTag.className = lowStockCount > 0 ? "kpi-tag warning" : "kpi-tag positive";
        }
    }

    renderCategoryBars() {
        const container = document.getElementById("category-bars-list");
        if (!container) return;

        const catMap = {};
        let totalUnits = 0;

        this.categories.forEach(c => {
            catMap[c.name] = { name: c.name, units: 0, itemsCount: 0 };
        });

        this.items.forEach(i => {
            const catName = i.category || "General";
            if (!catMap[catName]) {
                catMap[catName] = { name: catName, units: 0, itemsCount: 0 };
            }
            catMap[catName].units += i.quantity;
            catMap[catName].itemsCount++;
            totalUnits += i.quantity;
        });

        const colors = ["#4361ee", "#10b981", "#f59e0b", "#8b5cf6", "#ec4899", "#06b6d4"];
        let colorIdx = 0;

        const html = Object.values(catMap).map(cat => {
            const pct = totalUnits > 0 ? Math.round((cat.units / totalUnits) * 100) : 0;
            const barColor = colors[colorIdx % colors.length];
            colorIdx++;

            return `
                <div class="cat-bar-item">
                    <div class="cat-bar-meta">
                        <span>${cat.name} <small class="text-muted">(${cat.itemsCount} items)</small></span>
                        <span>${cat.units.toLocaleString()} units <small class="text-muted">(${pct}%)</small></span>
                    </div>
                    <div class="cat-bar-track">
                        <div class="cat-bar-fill" style="width: ${pct}%; background-color: ${barColor};"></div>
                    </div>
                </div>
            `;
        }).join("");

        container.innerHTML = html;
        document.getElementById("dash-cat-count").textContent = `${this.categories.length} Categories`;
    }

    renderLowStockFeed() {
        const feedList = document.getElementById("low-stock-feed-list");
        if (!feedList) return;

        const lowItems = this.items
            .filter(i => i.quantity <= i.reorderThreshold)
            .sort((a, b) => a.quantity - b.quantity);

        if (lowItems.length === 0) {
            feedList.innerHTML = `<div class="p-4 text-center text-muted">All inventory items are currently well-stocked.</div>`;
            return;
        }

        feedList.innerHTML = lowItems.slice(0, 5).map(item => {
            const isCritical = item.quantity <= 5;
            const initials = item.name.split(" ").slice(0, 2).map(w => w[0]).join("");
            const badgeClass = isCritical ? "stock-tag-critical" : "stock-tag-low";
            const badgeText = `${item.quantity} left (Min: ${item.reorderThreshold})`;

            return `
                <div class="alert-feed-item">
                    <div class="alert-item-left">
                        <div class="item-chip-avatar">${initials}</div>
                        <div class="alert-item-info">
                            <h4>${item.name}</h4>
                            <p>SKU: #${item.sku} • ${item.supplier || "Supplier not linked"}</p>
                        </div>
                    </div>
                    <div class="alert-item-right">
                        <span class="${badgeClass}">${badgeText}</span>
                        <button class="btn btn-xs btn-action-in" onclick="app.openStockActionModal(${item.id}, 'STOCK_IN')">
                            + Restock
                        </button>
                    </div>
                </div>
            `;
        }).join("");
    }

    renderRecentTransactions() {
        const tbody = document.getElementById("recent-transactions-tbody");
        if (!tbody) return;

        const recents = this.transactions.slice(0, 4);
        tbody.innerHTML = recents.map(t => {
            const isOut = t.type === "STOCK_OUT";
            const typeBadge = isOut 
                ? `<span class="badge-status critical">Stock Out</span>`
                : `<span class="badge-status instock">Stock In</span>`;

            return `
                <tr>
                    <td><strong>#${t.txnCode}</strong></td>
                    <td>
                        <div class="item-cell-title">${t.itemName}</div>
                        <div class="item-cell-sku">SKU: #${t.sku}</div>
                    </td>
                    <td>${typeBadge}</td>
                    <td><strong>${isOut ? "-" : "+"}${t.qty}</strong> units</td>
                    <td>₹${t.total.toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                    <td>${t.counter}</td>
                    <td class="text-muted">${t.date}</td>
                </tr>
            `;
        }).join("");
    }

    renderItemsTable() {
        const tbody = document.getElementById("items-table-tbody");
        if (!tbody) return;

        let filtered = this.items.filter(item => {
            if (this.searchQuery) {
                const matchName = item.name.toLowerCase().includes(this.searchQuery);
                const matchSku = item.sku.toLowerCase().includes(this.searchQuery);
                const matchCat = (item.category || "").toLowerCase().includes(this.searchQuery);
                if (!matchName && !matchSku && !matchCat) return false;
            }

            if (this.currentFilter === "low" && item.quantity > item.reorderThreshold) {
                return false;
            }

            if (this.selectedCategory !== "all" && item.category !== this.selectedCategory) {
                return false;
            }

            if (this.selectedSupplier !== "all" && item.supplier !== this.selectedSupplier) {
                return false;
            }

            return true;
        });

        document.getElementById("items-table-count").textContent = `Showing ${filtered.length} of ${this.items.length} items`;

        if (filtered.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="text-center p-6 text-muted">No items matched your search criteria.</td></tr>`;
            return;
        }

        tbody.innerHTML = filtered.map(item => {
            let statusBadge = "";
            if (item.quantity === 0) {
                statusBadge = `<span class="badge-status critical">Out of Stock</span>`;
            } else if (item.quantity <= 5) {
                statusBadge = `<span class="badge-status critical">Critical (${item.quantity})</span>`;
            } else if (item.quantity <= item.reorderThreshold) {
                statusBadge = `<span class="badge-status low">Low Stock (${item.quantity})</span>`;
            } else {
                statusBadge = `<span class="badge-status instock">In Stock</span>`;
            }

            return `
                <tr>
                    <td>
                        <div class="item-cell-title">${item.name}</div>
                        <div class="item-cell-sku">SKU: #${item.sku}</div>
                    </td>
                    <td><span class="badge-category-count">${item.category}</span></td>
                    <td><strong>${item.quantity}</strong> <small class="text-muted">/ min ${item.reorderThreshold}</small></td>
                    <td>₹${Number(item.unitPrice).toFixed(2)}</td>
                    <td class="text-muted">${item.supplier || "—"}</td>
                    <td>${statusBadge}</td>
                    <td class="text-right">
                        <div class="action-buttons-group">
                            <button class="btn btn-xs btn-action-in" title="Add Stock (Inward)" onclick="app.openStockActionModal(${item.id}, 'STOCK_IN')">+ In</button>
                            <button class="btn btn-xs btn-action-out" title="Deduct Stock (Sale / Outward)" onclick="app.openStockActionModal(${item.id}, 'STOCK_OUT')">- Out</button>
                            <button class="btn btn-xs btn-action-del" title="Delete Item" onclick="app.deleteItem(${item.id})">Del</button>
                        </div>
                    </td>
                </tr>
            `;
        }).join("");
    }

    renderSuppliersGrid() {
        const grid = document.getElementById("suppliers-cards-grid");
        if (!grid) return;

        grid.innerHTML = this.suppliers.map(s => {
            const itemCount = this.items.filter(i => i.supplierId === s.id).length;
            return `
                <div class="supplier-card">
                    <div class="supplier-card-header">
                        <div>
                            <div class="supplier-name">${s.name}</div>
                            <small class="text-muted">ID: #SUP-${s.id}</small>
                        </div>
                        <span class="badge-status instock">Active</span>
                    </div>
                    <div class="supplier-detail-row">
                        <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                        <span>${s.contact}</span>
                    </div>
                    <div class="supplier-detail-row">
                        <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path></svg>
                        <span>${s.phone}</span>
                    </div>
                    <div class="supplier-detail-row">
                        <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>
                        <span>${s.email}</span>
                    </div>
                    <div class="supplier-detail-row text-muted">
                        <small>📍 ${s.address}</small>
                    </div>
                    <div class="supplier-card-footer mt-auto pt-2 border-top">
                        <small class="text-muted">Supplies <strong>${itemCount}</strong> products in catalog</small>
                    </div>
                </div>
            `;
        }).join("");
    }

    renderTransactionsTable() {
        const tbody = document.getElementById("full-transactions-tbody");
        if (!tbody) return;

        let list = this.transactions;
        if (this.txnFilter !== "all") {
            list = list.filter(t => t.type === this.txnFilter);
        }

        tbody.innerHTML = list.map(t => {
            const isOut = t.type === "STOCK_OUT";
            const badge = isOut 
                ? `<span class="badge-status critical">STOCK OUT</span>`
                : `<span class="badge-status instock">STOCK IN</span>`;

            return `
                <tr>
                    <td><strong>#${t.txnCode}</strong></td>
                    <td class="text-muted">${t.date}</td>
                    <td>${badge}</td>
                    <td>
                        <div class="item-cell-title">${t.itemName}</div>
                        <div class="item-cell-sku">SKU: #${t.sku}</div>
                    </td>
                    <td><strong>${isOut ? "-" : "+"}${t.qty}</strong></td>
                    <td>₹${Number(t.unitPrice).toFixed(2)}</td>
                    <td><strong>₹${Number(t.total).toFixed(2)}</strong></td>
                    <td>${t.counter}</td>
                    <td class="text-muted">${t.remarks}</td>
                </tr>
            `;
        }).join("");
    }

    // ================================================================
    // ACTIONS: ADD, UPDATE, DELETE
    // ================================================================
    handleAddItem(e) {
        e.preventDefault();
        const sku = document.getElementById("new-sku").value.trim();
        const name = document.getElementById("new-name").value.trim();
        const catId = parseInt(document.getElementById("new-category").value);
        const qty = parseInt(document.getElementById("new-quantity").value);
        const price = parseFloat(document.getElementById("new-price").value);
        const threshold = parseInt(document.getElementById("new-threshold").value);
        const supId = parseInt(document.getElementById("new-supplier").value);

        if (this.items.some(i => i.sku.toUpperCase() === sku.toUpperCase())) {
            this.showToast(`Product with SKU '${sku}' already exists!`, "error");
            return;
        }

        const categoryObj = this.categories.find(c => c.id === catId);
        const supplierObj = this.suppliers.find(s => s.id === supId);

        const newItem = {
            id: Date.now(),
            sku: sku,
            name: name,
            categoryId: catId,
            category: categoryObj ? categoryObj.name : "General",
            quantity: qty,
            unitPrice: price,
            reorderThreshold: threshold,
            supplierId: supId,
            supplier: supplierObj ? supplierObj.name : "N/A"
        };

        this.items.unshift(newItem);

        if (qty > 0) {
            this.transactions.unshift({
                id: Date.now(),
                txnCode: `TXN-IN-${Math.floor(1000 + Math.random() * 9000)}`,
                itemId: newItem.id,
                itemName: name,
                sku: sku,
                type: "STOCK_IN",
                qty: qty,
                unitPrice: price,
                total: qty * price,
                counter: "Warehouse Receiving",
                remarks: "Initial inventory setup",
                date: new Date().toLocaleString()
            });
        }

        // Post to backend asynchronously if alive
        fetch("/api/items", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(newItem)
        }).catch(() => {});

        this.closeAllModals();
        e.target.reset();
        this.renderAll();
        this.showToast(`Added '${name}' to inventory successfully!`, "success");
    }

    openStockActionModal(itemId, actionType) {
        const item = this.items.find(i => i.id === itemId);
        if (!item) return;

        document.getElementById("action-item-id").value = item.id;
        document.getElementById("action-txn-type").value = actionType;
        document.getElementById("action-item-name").textContent = item.name;
        document.getElementById("action-item-qty").textContent = `${item.quantity} units`;

        const isOut = actionType === "STOCK_OUT";
        document.getElementById("stock-modal-title").textContent = isOut ? "Record Stock Out (Sale / Dispatch)" : "Record Stock In (Procurement / Restock)";
        document.getElementById("action-qty-label").textContent = isOut ? "Units to Deduct *" : "Units to Receive *";
        document.getElementById("btn-submit-stock-action").textContent = isOut ? "Confirm Stock Out" : "Confirm Stock In";
        document.getElementById("action-counter").value = isOut ? "Billing Counter 1" : "Warehouse Receiving";
        document.getElementById("action-remarks").value = isOut ? "Customer checkout sale" : "Procurement restock";

        this.openModal("modal-stock-action");
    }

    handleStockAction(e) {
        e.preventDefault();
        const itemId = parseInt(document.getElementById("action-item-id").value);
        const type = document.getElementById("action-txn-type").value;
        const qty = parseInt(document.getElementById("action-quantity").value);
        const counter = document.getElementById("action-counter").value.trim() || "Main Counter";
        const remarks = document.getElementById("action-remarks").value.trim() || "Stock update";

        const item = this.items.find(i => i.id === itemId);
        if (!item) return;

        if (type === "STOCK_OUT") {
            if (item.quantity < qty) {
                this.showToast(`Insufficient stock! Requested: ${qty}, but only ${item.quantity} available.`, "error");
                return;
            }
            item.quantity -= qty;
        } else {
            item.quantity += qty;
        }

        const total = qty * item.unitPrice;
        const prefix = type === "STOCK_IN" ? "IN" : "OUT";
        const newTxn = {
            id: Date.now(),
            txnCode: `TXN-${prefix}-${Math.floor(1000 + Math.random() * 9000)}`,
            itemId: item.id,
            itemName: item.name,
            sku: item.sku,
            type: type,
            qty: qty,
            unitPrice: item.unitPrice,
            total: total,
            counter: counter,
            remarks: remarks,
            date: new Date().toLocaleString()
        };

        this.transactions.unshift(newTxn);

        const apiPath = type === "STOCK_IN" ? "/api/transactions/in" : "/api/transactions/out";
        fetch(apiPath, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ itemId: item.id, quantity: qty, counter: counter, remarks: remarks })
        }).catch(() => {});

        this.closeAllModals();
        this.renderAll();
        this.showToast(`${type === "STOCK_IN" ? "Stocked In" : "Dispatched"} ${qty} units of ${item.name}!`, "success");
    }

    deleteItem(itemId) {
        const item = this.items.find(i => i.id === itemId);
        if (!item) return;

        if (!confirm(`Are you sure you want to remove '${item.name}' from inventory?`)) return;

        this.items = this.items.filter(i => i.id !== itemId);
        fetch(`/api/items/${itemId}`, { method: "DELETE" }).catch(() => {});

        this.renderAll();
        this.showToast(`Deleted '${item.name}' from inventory.`, "warning");
    }

    handleAddSupplier(e) {
        e.preventDefault();
        const name = document.getElementById("sup-name").value.trim();
        const contact = document.getElementById("sup-contact").value.trim();
        const phone = document.getElementById("sup-phone").value.trim();
        const email = document.getElementById("sup-email").value.trim();
        const address = document.getElementById("sup-address").value.trim();

        const newSup = {
            id: Date.now(),
            name: name,
            contact: contact || "Representative",
            phone: phone,
            email: email || "sales@partner.com",
            address: address || "Indore, MP"
        };

        this.suppliers.push(newSup);
        this.closeAllModals();
        this.populateDropdowns();
        this.renderSuppliersGrid();
        this.renderKPIs();
        this.showToast(`Supplier '${name}' added successfully!`, "success");
    }

    // ================================================================
    // REPORT GENERATOR
    // ================================================================
    buildAsciiReport() {
        let totalUnits = 0;
        let totalValuation = 0;
        let lowStockCount = 0;

        const catMap = {};
        this.categories.forEach(c => {
            catMap[c.name] = { name: c.name, count: 0, units: 0, val: 0 };
        });

        this.items.forEach(i => {
            totalUnits += i.quantity;
            totalValuation += i.quantity * i.unitPrice;
            if (i.quantity <= i.reorderThreshold) lowStockCount++;

            const cName = i.category || "General";
            if (!catMap[cName]) catMap[cName] = { name: cName, count: 0, units: 0, val: 0 };
            catMap[cName].count++;
            catMap[cName].units += i.quantity;
            catMap[cName].val += (i.quantity * i.unitPrice);
        });

        const dateStr = new Date().toLocaleString();

        let out = `========================================================================================\n`;
        out += `                       SMART INVENTORY MANAGEMENT SYSTEM - REPORT                      \n`;
        out += `                     Global Institute of Technology & Management                        \n`;
        out += `              Summer Internship Partner: Cypher Analytica, Indore                       \n`;
        out += `                             Generated: ${dateStr}\n`;
        out += `========================================================================================\n\n`;

        out += `--- EXECUTIVE SUMMARY ---\n`;
        out += `  Total Unique Products : ${this.items.length}\n`;
        out += `  Total Stock Quantity  : ${totalUnits.toLocaleString()} Units\n`;
        out += `  Active Categories     : ${this.categories.length}\n`;
        out += `  Low Stock Alert Items : ${lowStockCount}\n`;
        out += `  Total Inventory Value : ₹${totalValuation.toLocaleString('en-IN', { minimumFractionDigits: 2 })}\n\n`;

        out += `--- STOCK LEVELS BY CATEGORY ---\n`;
        out += `Category           | Items Count | Total Units  | Valuation (₹)\n`;
        out += `----------------------------------------------------------------------------------------\n`;
        Object.values(catMap).forEach(c => {
            out += `${c.name.padEnd(18)} | ${String(c.count).padEnd(11)} | ${String(c.units).padEnd(12)} | ₹${c.val.toFixed(2)}\n`;
        });
        out += `----------------------------------------------------------------------------------------\n\n`;

        out += `--- LOW STOCK ALERTS (ACTION REQUIRED) ---\n`;
        const lowItems = this.items.filter(i => i.quantity <= i.reorderThreshold);
        if (lowItems.length === 0) {
            out += `  All items are currently above reorder threshold.\n`;
        } else {
            out += `SKU      | Item Name                | Current  | Threshold  | Supplier\n`;
            out += `----------------------------------------------------------------------------------------\n`;
            lowItems.forEach(i => {
                out += `${i.sku.padEnd(8)} | ${i.name.padEnd(24)} | ${String(i.quantity).padEnd(8)} | ${String(i.reorderThreshold).padEnd(10)} | ${i.supplier}\n`;
            });
        }
        out += `\n========================================================================================\n`;
        return out;
    }

    renderAsciiReport() {
        const box = document.getElementById("ascii-report-content");
        if (box) {
            box.textContent = this.buildAsciiReport();
        }
    }

    downloadReportFile() {
        const content = this.buildAsciiReport();
        const blob = new Blob([content], { type: "text/plain" });
        const url = URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = `Inventory_Report_${new Date().toISOString().split("T")[0]}.txt`;
        a.click();
        URL.revokeObjectURL(url);
        this.showToast("Report downloaded successfully!", "success");
    }

    // ================================================================
    // MODAL UTILITIES & TOAST
    // ================================================================
    openModal(modalId) {
        document.getElementById(modalId)?.classList.add("open");
    }

    closeAllModals() {
        document.querySelectorAll(".modal-overlay").forEach(m => m.classList.remove("open"));
    }

    showToast(message, type = "info") {
        const container = document.getElementById("toast-container");
        if (!container) return;

        const toast = document.createElement("div");
        toast.className = `toast ${type}`;
        toast.textContent = message;

        container.appendChild(toast);
        setTimeout(() => {
            toast.style.opacity = "0";
            toast.style.transform = "translateX(100%)";
            toast.style.transition = "all 0.3s";
            setTimeout(() => toast.remove(), 300);
        }, 3200);
    }
}

// Global App Initialization
const app = new InventoryApp();
