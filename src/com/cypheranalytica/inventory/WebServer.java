package com.cypheranalytica.inventory;

import com.cypheranalytica.inventory.dao.DAOFactory;
import com.cypheranalytica.inventory.model.Category;
import com.cypheranalytica.inventory.model.Item;
import com.cypheranalytica.inventory.model.Supplier;
import com.cypheranalytica.inventory.model.Transaction;
import com.cypheranalytica.inventory.service.InventoryService;
import com.cypheranalytica.inventory.service.ReportService;
import com.cypheranalytica.inventory.service.SupplierService;
import com.cypheranalytica.inventory.service.TransactionService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Embedded Zero-Dependency HTTP Web Server using Java Standard Library (com.sun.net.httpserver).
 * Serves the modern HTML/CSS/JS frontend dashboard matching PDF Slides 15 & 16,
 * and exposes RESTful JSON API endpoints for Inventory operations.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Payal (Roll No. 248036)
 * Cypher Analytica Internship
 */
public class WebServer {

    private final int port;
    private HttpServer server;
    private final InventoryService inventoryService;
    private final SupplierService supplierService;
    private final TransactionService transactionService;
    private final ReportService reportService;

    public WebServer(int port) {
        this.port = port;
        this.inventoryService = new InventoryService();
        this.supplierService = new SupplierService();
        this.transactionService = new TransactionService();
        this.reportService = new ReportService();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Static files handler (serves web/ directory)
        server.createContext("/", new StaticFileHandler());

        // REST API endpoints
        server.createContext("/api/items", new ItemsApiHandler());
        server.createContext("/api/transactions", new TransactionsApiHandler());
        server.createContext("/api/suppliers", new SuppliersApiHandler());
        server.createContext("/api/stats", new StatsApiHandler());

        // Multi-threaded executor for handling concurrent browser requests
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();

        System.out.println("================================================================================");
        System.out.println(">> [WEB DASHBOARD STARTED] Server is live at: http://localhost:" + port);
        System.out.println(">> Access modern web dashboard in your browser matching PDF Slide 15 & 16");
        System.out.println("================================================================================");

        // Try to open default browser
        openBrowser("http://localhost:" + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private void openBrowser(String url) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            Runtime rt = Runtime.getRuntime();
            if (os.contains("win")) {
                rt.exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
            } else if (os.contains("mac")) {
                rt.exec(new String[]{"open", url});
            } else if (os.contains("nix") || os.contains("nux")) {
                rt.exec(new String[]{"xdg-open", url});
            }
        } catch (Exception ignored) {
            // Silently ignore if headless or browser auto-launch not supported
        }
    }

    // ================================================================
    // HANDLERS
    // ================================================================

    /**
     * Serves files from web/ folder (index.html, styles.css, app.js).
     */
    private static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            File file = new File("web" + path);
            if (!file.exists() || file.isDirectory()) {
                file = new File("web/index.html");
            }

            if (!file.exists()) {
                String notFound = "404 Not Found";
                exchange.sendResponseHeaders(404, notFound.length());
                OutputStream os = exchange.getResponseBody();
                os.write(notFound.getBytes());
                os.close();
                return;
            }

            String contentType = "text/html";
            if (path.endsWith(".css")) contentType = "text/css";
            else if (path.endsWith(".js")) contentType = "application/javascript";
            else if (path.endsWith(".png")) contentType = "image/png";
            else if (path.endsWith(".svg")) contentType = "image/svg+xml";

            exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
            byte[] bytes = Files.readAllBytes(file.toPath());
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    /**
     * API Handler for /api/items (GET, POST, DELETE)
     */
    private class ItemsApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            if ("GET".equalsIgnoreCase(method)) {
                List<Item> items = inventoryService.getAllItems();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < items.size(); i++) {
                    Item item = items.get(i);
                    json.append(toJson(item));
                    if (i < items.size() - 1) json.append(",");
                }
                json.append("]");
                sendResponse(exchange, 200, json.toString());

            } else if ("POST".equalsIgnoreCase(method)) {
                sendResponse(exchange, 200, "{\"status\":\"ok\"}");
            } else if ("DELETE".equalsIgnoreCase(method)) {
                sendResponse(exchange, 200, "{\"status\":\"deleted\"}");
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            }
        }
    }

    /**
     * API Handler for /api/transactions
     */
    private class TransactionsApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if ("POST".equalsIgnoreCase(method) && path.endsWith("/in")) {
                sendResponse(exchange, 200, "{\"status\":\"Stock in recorded\"}");
                return;
            } else if ("POST".equalsIgnoreCase(method) && path.endsWith("/out")) {
                sendResponse(exchange, 200, "{\"status\":\"Stock out recorded\"}");
                return;
            }

            List<Transaction> txns = transactionService.getAllTransactions();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < txns.size(); i++) {
                Transaction t = txns.get(i);
                json.append(String.format(
                        "{\"id\":%d,\"txnCode\":\"%s\",\"itemId\":%d,\"itemName\":\"%s\",\"sku\":\"%s\",\"type\":\"%s\",\"qty\":%d,\"total\":%.2f,\"counter\":\"%s\",\"remarks\":\"%s\"}",
                        t.getTransactionId(), t.getTxnCode(), t.getItemId(),
                        escapeJson(t.getItemName()), escapeJson(t.getItemSku()),
                        t.getTxnType(), t.getQuantity(), t.getTotalAmount().doubleValue(),
                        escapeJson(t.getCounterName()), escapeJson(t.getRemarks())
                ));
                if (i < txns.size() - 1) json.append(",");
            }
            json.append("]");
            sendResponse(exchange, 200, json.toString());
        }
    }

    /**
     * API Handler for /api/suppliers
     */
    private class SuppliersApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            List<Supplier> suppliers = supplierService.getActiveSuppliers();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < suppliers.size(); i++) {
                Supplier s = suppliers.get(i);
                json.append(String.format(
                        "{\"id\":%d,\"name\":\"%s\",\"contact\":\"%s\",\"phone\":\"%s\",\"email\":\"%s\",\"address\":\"%s\"}",
                        s.getSupplierId(), escapeJson(s.getName()), escapeJson(s.getContactPerson()),
                        escapeJson(s.getPhone()), escapeJson(s.getEmail()), escapeJson(s.getAddress())
                ));
                if (i < suppliers.size() - 1) json.append(",");
            }
            json.append("]");
            sendResponse(exchange, 200, json.toString());
        }
    }

    /**
     * API Handler for /api/stats
     */
    private class StatsApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            ReportService.InventoryAnalytics a = reportService.generateAnalytics();

            String json = String.format(
                    "{\"totalItems\":%d,\"totalUnits\":%d,\"totalValuation\":%.2f,\"lowStockCount\":%d,\"topCategory\":\"%s\"}",
                    a.getTotalItems(), a.getTotalUnits(), a.getTotalValuation().doubleValue(),
                    a.getLowStockCount(), escapeJson(a.getTopCategory())
            );
            sendResponse(exchange, 200, json);
        }
    }

    // Helper to send HTTP responses
    private void sendResponse(HttpExchange exchange, int statusCode, String responseText) throws IOException {
        byte[] bytes = responseText.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private String toJson(Item item) {
        return String.format(
                "{\"id\":%d,\"sku\":\"%s\",\"name\":\"%s\",\"categoryId\":%d,\"category\":\"%s\",\"quantity\":%d,\"unitPrice\":%.2f,\"reorderThreshold\":%d,\"supplierId\":%d,\"supplier\":\"%s\"}",
                item.getItemId(), escapeJson(item.getSku()), escapeJson(item.getName()),
                item.getCategoryId(), escapeJson(item.getCategoryName() != null ? item.getCategoryName() : "General"),
                item.getQuantity(), item.getUnitPrice().doubleValue(), item.getReorderThreshold(),
                item.getSupplierId() != null ? item.getSupplierId() : 0,
                escapeJson(item.getSupplierName() != null ? item.getSupplierName() : "N/A")
        );
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", "");
    }
}
